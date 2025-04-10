package com.open.ai.eros.pay.order.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.util.DistributedLockUtils;
import com.open.ai.eros.common.util.HttpUtil;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.CommonStatusEnum;
import com.open.ai.eros.db.constants.GoodTypeEnum;
import com.open.ai.eros.db.constants.OrderStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.Goods;
import com.open.ai.eros.db.mysql.pay.entity.GoodsSnapshot;
import com.open.ai.eros.db.mysql.pay.entity.Order;
import com.open.ai.eros.db.mysql.pay.service.impl.*;
import com.open.ai.eros.pay.config.PayConfig;
import com.open.ai.eros.pay.goods.bean.vo.CGoodsVo;
import com.open.ai.eros.pay.goods.bean.vo.CRightsVo;
import com.open.ai.eros.pay.goods.convert.GoodsConvert;
import com.open.ai.eros.pay.goods.manager.GoodsManager;
import com.open.ai.eros.pay.order.bean.dto.GetOutOrderDto;
import com.open.ai.eros.pay.order.bean.dto.GetOutOrderResponseDto;
import com.open.ai.eros.pay.order.bean.dto.GetPayUrlDto;
import com.open.ai.eros.pay.order.bean.dto.GetPayUrlResponseDto;
import com.open.ai.eros.pay.order.bean.req.OrderSearchReq;
import com.open.ai.eros.pay.order.bean.vo.GetPayUrlVo;
import com.open.ai.eros.pay.order.bean.vo.OrderQuerySearchResult;
import com.open.ai.eros.pay.order.bean.vo.OrderVo;
import com.open.ai.eros.pay.order.convert.OrderConvert;
import com.open.ai.eros.pay.util.PayHttpUtil;
import com.open.ai.eros.user.constants.UserBalanceRecordEnum;
import com.open.ai.eros.user.manager.UserBalanceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @类名：OrderManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/26 12:18
 */
@Slf4j
@Component
public class OrderManager {


    @Autowired
    private OrderServiceImpl orderService;


    @Autowired
    private GoodsServiceImpl goodsService;

    @Autowired
    private GoodsSnapshotServiceImpl goodsSnapshotService;

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private PayHttpUtil payHttpUtil;

    @Autowired
    private PayConfig payConfig;

    @Autowired
    private CurrencyRateServiceImpl currencyRateService;

    @Autowired
    private UserRightsServiceImpl userRightsService;

    @Autowired
    private UserBalanceManager userBalanceManager;



    /**
     * 根据订单id
     *
     * 更新订单状态为 支付成功
     * @param orderId
     */
    public void updateOrderStatusToPaySuccess(Long orderId){
        Order order = orderService.getById(orderId);
        if(order==null){
            return;
        }
        updateOrderStatusToPaySuccess(order);
    }


    /**
     * 根据用户id + 流水号
     *
     * @param userId
     * @param code
     */
    public void updateOrderStatusToPaySuccess(Long userId,String code){
        Order order = orderService.getOrderByCode(userId,code);
        if(order==null){
            return;
        }
        updateOrderStatusToPaySuccess(order);
    }


    /**
     * 更新订单状态为 支付成功
     *
     * @param order
     */
    public void updateOrderStatusToPaySuccess(Order order){
        try {
            GetOutOrderResponseDto outOrderResponseDto = payHttpUtil.queryOrderStatus(GetOutOrderDto.builder().trade_no(String.valueOf(order.getId())).build());
            if(outOrderResponseDto==null || outOrderResponseDto.getStatus()!=1){
                return;
            }
            // 更新订单状态-》完成
            boolean updated = orderService.updateOrderStatusToPaySuccess(order.getId());
            if(updated){
                goodsService.updateGoodsSeedNum(order.getGoodsId());
            }
        }catch (Exception e){
            log.error("updateOrderStatusByOutResult error 更新订单状态失败 awaitOrder={}", JSONObject.toJSONString(order),e);
        }
    }

    /**
     * 更新商品状态-》已完成
     * 发放商品给用户
     * @param orderId
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatusToDoneAndProvideGoodsToUser(Long orderId){

        Order order = orderService.getById(orderId);
        if(order==null || !order.getStatus().equals(OrderStatusEnum.PAY_SUCCESS.getStatus())){
            return;
        }
        Lock lock = DistributedLockUtils.getLock("orderGoodsProvide:" + orderId, 10);
        if(lock.tryLock()){
            try {
                order = orderService.getById(orderId);
                if(order==null || !order.getStatus().equals(OrderStatusEnum.PAY_SUCCESS.getStatus())){
                    return;
                }
                try {

                    Long goodSnapshotId = order.getGoodSnapshotId();
                    Long userId = order.getUserId();

                    int result = orderService.getBaseMapper().updateOrderStatusToDone(orderId,OrderStatusEnum.DONE.getStatus());
                    log.info("updateOrderStatusToDone id={},result={}",orderId,result);
                    if(result<=0){
                        return;
                    }

                    GoodsSnapshot goodsSnapshot = goodsSnapshotService.getById(goodSnapshotId);
                    if(goodsSnapshot==null){
                        return;
                    }

                    String type = goodsSnapshot.getType();
                    if(type.equals(GoodTypeEnum.RIGHTS.getType())){
                        //权益商品
                        String goodValue = goodsSnapshot.getGoodValue();
                        String[] split = goodValue.split(",");
                        for (String snapshotRightId : split) {
                            userRightsService.addUserRightsBySnapshotId(Long.parseLong(snapshotRightId),userId);
                        }
                    }else if(type.equals(GoodTypeEnum.COMMON.getType())){
                        //普通商品 新增用户不可提现余额
                        String goodValue = goodsSnapshot.getGoodValue();
                        userBalanceManager.addUserNonWithDrawAbleBalance(userId,Long.parseLong(goodValue), UserBalanceRecordEnum.BUY_GOODS);
                    }

                }catch (Exception e){
                    log.error("updateOrderStatusToDone error orderId={}",order.getId(),e);
                }
            }finally {
                lock.unlock();
            }
        }
    }


    /**
     * 是否有超时订单
     *
     * @param endTime
     * @return
     */
    public boolean haveTimeOutOrder(Date startTime,Date endTime){
        int haveTimeOutOrder = orderService.getBaseMapper().haveTimeOutOrder(startTime,endTime, OrderStatusEnum.WAIT_PAY.getStatus());
        return haveTimeOutOrder>0;
    }

    /**
     * 更新超时订单状态
     *
     * @param endTime
     */
    public void updateTimeOutOrder(Date startTime,Date endTime){
        int updateTimeOutOrder = orderService.getBaseMapper().updateTimeOutOrder(startTime,endTime,OrderStatusEnum.WAIT_PAY.getStatus(),OrderStatusEnum.TIME_OUT.getStatus(),new Date());
        log.info("updateTimeOutOrder endTime={},updateTimeOutOrder={}", DateUtils.formatDate(endTime,DateUtils.FORMAT_YYYY_MM_DD_HHMMSS),updateTimeOutOrder);
    }


    /**
     * 通过订单号获取订单状态
     * @param orderId
     * @return
     */
    public ResultVO<OrderVo> getOrder(Long userId,Long orderId){
        Order order = orderService.getOrderById(userId, orderId);
        if(order==null){
            return ResultVO.fail("订单不存在！");
        }
        OrderVo orderVo = OrderConvert.I.convertOrderVo(order);
        return ResultVO.success(orderVo);
    }


    /**
     * 创建订单
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Transactional
    public ResultVO<GetPayUrlVo> createOrder(Long userId, Long goodsId,String payWay){
        Goods goods = goodsService.getById(goodsId);
        if(goods==null || goods.getStatus().equals(CommonStatusEnum.DELETE.getStatus())){
            return ResultVO.fail("下单的商品不存在！");
        }
        GoodsSnapshot goodsSnapshot = goodsSnapshotService.getNewGoodsSnapshot(goodsId);
        if(goodsSnapshot==null){
            return ResultVO.fail("下单的商品不存在！");
        }
        if(goods.getSeedNum()>=goods.getTotal()){
            return ResultVO.fail("商品已出售完！");
        }


        Lock lock = DistributedLockUtils.getLock("createOrder:"+userId, 30);
        if (lock.tryLock()) {
            try {
                Order payOrder = orderService.getOrderByPayStatus(userId, goodsId);
                if(payOrder!=null){
                    // 有未支付的订单
                    GetPayUrlVo getPayUrlVo = new GetPayUrlVo();
                    getPayUrlVo.setId(payOrder.getId());
                    getPayUrlVo.setPayUrl(payOrder.getPayUrl());
                    return ResultVO.success(getPayUrlVo);
                }

                Order order = new Order();
                order.setPrice(goodsSnapshot.getPrice());
                order.setUnit(goodsSnapshot.getUnit());
                order.setUserId(userId);
                order.setGoodSnapshotId(goodsSnapshot.getId());
                order.setGoodsId(goodsId);
                order.setCreateTime(LocalDateTime.now());
                order.setStatus(OrderStatusEnum.WAIT_PAY.getStatus());

                BigDecimal rate = currencyRateService.getRate(goodsSnapshot.getUnit());
                order.setRate(rate);

                // 转化为 元 因为目前聚合平台只支持 人民币结算
                BigDecimal cnyBigDecimal = rate.multiply(goodsSnapshot.getPrice()).setScale(2, RoundingMode.HALF_UP);

                boolean saveResult = orderService.save(order);
                if(!saveResult){
                    throw new BizException("订单保存失败！");
                }
                GetPayUrlResponseDto payUrlResponse = payHttpUtil.getOrderPayUrl(GetPayUrlDto.builder()
                        .pid(payConfig.getPid())
                        .clientip(HttpUtil.getIpAddress())
                        .name(goodsSnapshot.getName())
                        .money(cnyBigDecimal.toString())
                        .notify_url(payConfig.getNotifyUrl())
                        .type(payWay)
                        .out_trade_no(String.valueOf(order.getId()))
                        .build());

                String payUrl = StringUtils.isEmpty(payUrlResponse.getPayurl())?payUrlResponse.getQrcode():payUrlResponse.getPayurl();

                if(payUrlResponse.getCode() != 1 || StringUtils.isEmpty(payUrl)){
                    throw new BizException("发起支付失败");
                }

                Order updateOrder = new Order();
                updateOrder.setId(order.getId());
                updateOrder.setPayUrl(payUrl);
                updateOrder.setCode(payUrlResponse.getTrade_no());
                updateOrder.setUpdateTime(LocalDateTime.now());
                boolean updated = orderService.updateById(updateOrder);
                if(!updated){
                    throw new BizException("更新订单失败！");
                }

                GetPayUrlVo getPayUrlVo = new GetPayUrlVo();
                getPayUrlVo.setId(order.getId());
                getPayUrlVo.setPayUrl(payUrl);
                return ResultVO.success(getPayUrlVo);
            }finally {
                lock.unlock();
            }
        }
        return ResultVO.fail("服务繁忙！");

    }




    public ResultVO<OrderQuerySearchResult> list(Integer pageNum, Integer pageSize, Long userId, Integer status){
        OrderQuerySearchResult result = new OrderQuerySearchResult();

        LambdaQueryWrapper<Order> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Order::getUserId,userId);
        lambdaQueryWrapper.orderByDesc(Order::getCreateTime);
        if(status!=null){
            lambdaQueryWrapper.eq(Order::getStatus,status);
        }

        Page<Order> page = new Page<>(pageNum,pageSize);

        Page<Order> orderPage = orderService.page(page, lambdaQueryWrapper);
        if(orderPage.getRecords().size()<pageSize){
            result.setLastPage(true);
        }
        List<Order> records = orderPage.getRecords();

        List<Long> goodsIds = records.stream().map(Order::getGoodsId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(goodsIds)){
            return ResultVO.success();
        }

        List<Goods> goods = goodsService.listByIds(goodsIds);
        Map<Long, Goods> goodsMap = goods.stream().collect(Collectors.toMap(Goods::getId, e -> e, (k1, k2) -> k1));

        List<OrderVo> orderVos = new ArrayList<>(records.size());
        for (Order record : records) {
            Long goodsId = record.getGoodsId();
            Goods userGoods = goodsMap.get(goodsId);
            if(userGoods==null){
                continue;
            }
            OrderVo orderVo = OrderConvert.I.convertOrderVo(record);

            CGoodsVo cGoodsVo = GoodsConvert.I.convertCGoodsVo(userGoods);
            if(cGoodsVo.getType().equals(GoodTypeEnum.RIGHTS.getType())){
                List<CRightsVo> cRightsVos = goodsManager.adaptRights(userGoods.getGoodValue());
                cGoodsVo.setRightsVos(cRightsVos);
            }
            if(cGoodsVo.getType().equals(GoodTypeEnum.COMMON.getType())){
                BigDecimal num = new BigDecimal(cGoodsVo.getGoodValue());
                BigDecimal oneCodeBalance = num.divide(new BigDecimal(CommonConstant.ONE_DOLLAR));
                cGoodsVo.setGoodValue(BalanceFormatUtil.getUserBalance(oneCodeBalance.longValue()));
            }
            orderVo.setCGoodsVo(cGoodsVo);
            orderVos.add(orderVo);
        }
        result.setOrderVos(orderVos);
        return ResultVO.success(result);
    }

    public List<Order> orderRecord(Integer pageNum, Integer pageSize, Integer status,Date startTime,Date endTime){

        LambdaQueryWrapper<Order> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Order::getCreateTime);
        if(status!=null){
            lambdaQueryWrapper.eq(Order::getStatus,status);
        }
        lambdaQueryWrapper.between(Order::getCreateTime, startTime, endTime);
        Page<Order> page = new Page<>(pageNum,pageSize);
        Page<Order> orderPage = orderService.page(page, lambdaQueryWrapper);
        return orderPage.getRecords();
    }




    public ResultVO<PageVO<OrderVo>> orderList(OrderSearchReq req){
        PageVO<OrderVo> result = new PageVO<>();

        LambdaQueryWrapper<Order> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(req.getUserId())){
            lambdaQueryWrapper.eq(Order::getUserId,req.getUserId());
        }
        if (Objects.nonNull(req.getId())){
            lambdaQueryWrapper.eq(Order::getId,req.getId());
        }
        if (Objects.nonNull(req.getCode())){
            lambdaQueryWrapper.eq(Order::getCode,req.getCode());
        }
        if(req.getStatus()!=null){
            lambdaQueryWrapper.eq(Order::getStatus,req.getStatus());
        }
        if(req.getStartTime() != null){
            lambdaQueryWrapper.ge(Order::getCreateTime, new Date(req.getStartTime()));  // 大于等于开始时间
        }
        if(req.getEndTime() != null){
            lambdaQueryWrapper.le(Order::getCreateTime,new Date(req.getEndTime()));    // 小于等于结束时间
        }
        lambdaQueryWrapper.orderByDesc(Order::getCreateTime);


        Page<Order> page = new Page<>(req.getPageNum(),req.getPageSize());

        Page<Order> orderPage = orderService.page(page, lambdaQueryWrapper);
        result.setTotal(orderPage.getTotal());
        List<Order> records = orderPage.getRecords();

        List<Long> goodsIds = records.stream().map(Order::getGoodsId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(goodsIds)){
            return ResultVO.success(result);
        }

        List<Goods> goods = goodsService.listByIds(goodsIds);
        Map<Long, Goods> goodsMap = goods.stream().collect(Collectors.toMap(Goods::getId, e -> e, (k1, k2) -> k1));

        List<OrderVo> orderVos = new ArrayList<>(records.size());
        for (Order record : records) {
            Long goodsId = record.getGoodsId();
            Goods userGoods = goodsMap.get(goodsId);
            if(userGoods==null){
                continue;
            }
            OrderVo orderVo = OrderConvert.I.convertOrderVo(record);

            CGoodsVo cGoodsVo = GoodsConvert.I.convertCGoodsVo(userGoods);
            if(cGoodsVo.getType().equals(GoodTypeEnum.RIGHTS.getType())){
                List<CRightsVo> cRightsVos = goodsManager.adaptRights(userGoods.getGoodValue());
                cGoodsVo.setRightsVos(cRightsVos);
            }
            if(cGoodsVo.getType().equals(GoodTypeEnum.COMMON.getType())){
                BigDecimal num = new BigDecimal(cGoodsVo.getGoodValue());
                BigDecimal oneCodeBalance = num.divide(new BigDecimal(CommonConstant.ONE_DOLLAR));
                cGoodsVo.setGoodValue(BalanceFormatUtil.getUserBalance(oneCodeBalance.longValue()));
            }
            orderVo.setCGoodsVo(cGoodsVo);
            orderVos.add(orderVo);
        }
        result.setData(orderVos);
        return ResultVO.success(result);
    }


}
