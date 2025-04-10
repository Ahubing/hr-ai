package com.open.ai.eros.pay.goods.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.GoodTypeEnum;
import com.open.ai.eros.db.constants.CommonStatusEnum;
import com.open.ai.eros.db.constants.RightsRuleEnum;
import com.open.ai.eros.db.mysql.pay.entity.Goods;
import com.open.ai.eros.db.mysql.pay.entity.GoodsSnapshot;
import com.open.ai.eros.db.mysql.pay.entity.Rights;
import com.open.ai.eros.db.mysql.pay.entity.RightsSnapshot;
import com.open.ai.eros.db.mysql.pay.service.impl.GoodsServiceImpl;
import com.open.ai.eros.db.mysql.pay.service.impl.GoodsSnapshotServiceImpl;
import com.open.ai.eros.db.mysql.pay.service.impl.RightsServiceImpl;
import com.open.ai.eros.db.mysql.pay.service.impl.RightsSnapshotServiceImpl;
import com.open.ai.eros.pay.goods.bean.req.GoodsAddReq;
import com.open.ai.eros.pay.goods.bean.req.GoodsSearchReq;
import com.open.ai.eros.pay.goods.bean.req.GoodsUpdateReq;
import com.open.ai.eros.pay.goods.bean.vo.*;
import com.open.ai.eros.pay.goods.convert.GoodsConvert;
import com.open.ai.eros.pay.goods.convert.RightsConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @类名：GoodsManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/24 21:02
 */

@Component
@Slf4j
public class GoodsManager {


    @Autowired
    private GoodsServiceImpl goodsService;


    @Autowired
    private RightsServiceImpl rightsService;

    @Autowired
    private GoodsSnapshotServiceImpl goodsSnapshotService;


    @Autowired
    private RightsSnapshotServiceImpl rightsSnapshotService;


    /**
     * 下架商品
     *
     * @param goodId
     * @return
     */
    public ResultVO deleteGoods(Long goodId){
        Goods goods = new Goods();
        goods.setId(goodId);
        goods.setStatus(CommonStatusEnum.DELETE.getStatus());
        boolean updated = goodsService.updateById(goods);
        if(!updated){
            throw new BizException("下架失败！");
        }
        return ResultVO.success();
    }


    /**
     * 新增商品
     *
     * @param userId
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO addGoods(Long userId,GoodsAddReq req){
        if ("common".equals(req.getType())){
            BigDecimal num = new BigDecimal(req.getGoodValue());
            BigDecimal oneCodeBalance = num.multiply(new BigDecimal(CommonConstant.ONE_DOLLAR));
            req.setGoodValue(String.valueOf(oneCodeBalance.longValue()));
        }
        Goods goods = GoodsConvert.I.convertGoods(req);
        goods.setCreateTime(LocalDateTime.now());
        goods.setCreateUserId(userId);
        goods.setStatus(CommonStatusEnum.OK.getStatus());
        boolean saveResult = goodsService.save(goods);
        if(!saveResult){
            throw new BizException("新增商品失败");
        }
        GoodsSnapshot goodsSnapshot =  buildGoodsSnapshot(goods);
        saveResult = goodsSnapshotService.save(goodsSnapshot);
        if(!saveResult){
            throw new BizException("新增快照商品失败");
        }
        log.info("addGoods goods={}", JSONObject.toJSONString(goods));
        return ResultVO.success();
    }


    /**
     * 修改商品
     * @param userId
     * @param req
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateGoods(Long userId, GoodsUpdateReq req){
        if ("common".equals(req.getType())){
            BigDecimal num = new BigDecimal(req.getGoodValue());
            BigDecimal oneCodeBalance = num.multiply(new BigDecimal(CommonConstant.ONE_DOLLAR));
            req.setGoodValue(String.valueOf(oneCodeBalance.longValue()));
        }
        Goods goods = GoodsConvert.I.convertGoods(req);
        goods.setCreateTime(LocalDateTime.now());
        goods.setCreateUserId(userId);
        boolean updateResult = goodsService.updateById(goods);
        if(!updateResult){
            throw new BizException("修改商品");
        }
        GoodsSnapshot goodsSnapshot = buildGoodsSnapshot(goods);
        boolean saveResult = goodsSnapshotService.save(goodsSnapshot);
        if(!saveResult){
            throw new BizException("新增快照商品失败");
        }
        log.info("updateGoods goods={}", JSONObject.toJSONString(goods));
        return ResultVO.success();
    }


    public GoodsSnapshot buildGoodsSnapshot(Goods goods){
        GoodsSnapshot goodsSnapshot = GoodsConvert.I.convertGoodsSnapshot(goods);
        String type = goodsSnapshot.getType();
        if(!type.equals(GoodTypeEnum.RIGHTS.getType())){
            return goodsSnapshot;
        }
        String goodValue = goodsSnapshot.getGoodValue();
        List<Long> rightsIds = Arrays.stream(goodValue.split(",")).map(Long::parseLong).distinct().collect(Collectors.toList());

        if(CollectionUtils.isEmpty(rightsIds)){
            throw new BizException("权益id为空");
        }

        List<RightsSnapshot> rightsSnapshots = rightsSnapshotService.batchGetLastRightsSnapshot(rightsIds);
        if(CollectionUtils.isEmpty(rightsSnapshots)){
            throw new BizException("权益不存在！");
        }
        List<String> userRightIds = rightsSnapshots.stream().map(e->String.valueOf(e.getId())).collect(Collectors.toList());
        String join = String.join(",",userRightIds);
        goodsSnapshot.setGoodValue(join);
        return goodsSnapshot;
    }



    /**
     * 搜索商品
     *
     * @param pageNum
     * @param pageSize
     * @param type
     * @return
     */
    public ResultVO<GoodsSearchResult> cSearch(Integer pageNum,Integer pageSize,String type){
        GoodsSearchResult result = new GoodsSearchResult();
        LambdaQueryWrapper<Goods> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        Page<Goods> page = new Page<>(pageNum,pageSize);
        if(StringUtils.isNoneEmpty(type)){
            lambdaQueryWrapper.eq(Goods::getType,type);
        }
        lambdaQueryWrapper.eq(Goods::getStatus, CommonStatusEnum.OK.getStatus());
        lambdaQueryWrapper.orderByAsc(Goods::getPrice);

        Page<Goods> goodsPage = goodsService.page(page, lambdaQueryWrapper);
        if(goodsPage.getRecords().size()<pageSize){
            result.setLastPage(true);
        }
        List<CGoodsVo> cGoodsVos = new ArrayList<>();
        for (Goods e : goodsPage.getRecords()) {
            CGoodsVo cGoodsVo = GoodsConvert.I.convertCGoodsVo(e);
            if(cGoodsVo.getType().equals(GoodTypeEnum.RIGHTS.getType())){
                cGoodsVo.setRightsVos(adaptRights(e.getGoodValue()));
            }else{
                cGoodsVo.setGoodValue(BalanceFormatUtil.getUserBalance(Long.parseLong(e.getGoodValue())));
            }
            if (e.getTotal()-e.getSeedNum() >0){
                cGoodsVos.add(cGoodsVo);
            }
        }
        result.setGoodsVos(cGoodsVos);
        return ResultVO.success(result);
    }


    /**
     * 分页搜索
     *
     * @param req
     * @return
     */
    public ResultVO<PageVO<GoodsVo>> searchGoods(GoodsSearchReq req){
        String type = req.getType();
        Long id = req.getId();
        Integer status = req.getStatus();
        Integer pageSize = req.getPageSize();
        Integer pageNum = req.getPageNum();
        LambdaQueryWrapper<Goods> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        Page<Goods> page = new Page<>(pageNum,pageSize);
        if(StringUtils.isNoneEmpty(type)){
            lambdaQueryWrapper.eq(Goods::getType,type);
        }
        if(status!=null){
            lambdaQueryWrapper.eq(Goods::getStatus,status);
        }
        if(id!=null){
            lambdaQueryWrapper.eq(Goods::getId,id);
        }

        Page<Goods> goodsPage = goodsService.page(page, lambdaQueryWrapper);

        List<GoodsVo> goodsVos = goodsPage.getRecords().stream().map(e->{
            GoodsVo goodsVo = GoodsConvert.I.convertGoodsVo(e);
            if(goodsVo.getType().equals(GoodTypeEnum.RIGHTS.getType())){
                goodsVo.setRightsVos(adaptRights(e.getGoodValue()));
            }
            goodsVo.setGoodValue(e.getGoodValue());
            if ("common".equals(e.getType())){
                BigDecimal num = new BigDecimal(e.getGoodValue());
                BigDecimal oneCodeBalance = num.divide(new BigDecimal(CommonConstant.ONE_DOLLAR));
                goodsVo.setGoodValue(oneCodeBalance.toString());
            }
            goodsVo.setStatus(e.getStatus());
            return goodsVo;
        }).collect(Collectors.toList());

        return ResultVO.success(PageVO.build(goodsPage.getTotal(),goodsVos));
    }



    /**
     * 适配权益
     * @param goodValue
     * @return
     */
    public List<CRightsVo> adaptRights(String goodValue){

        List<Long> rightsIds = Arrays.stream(goodValue.split(",")).map(Long::parseLong).distinct().collect(Collectors.toList());

        if(CollectionUtils.isEmpty(rightsIds)){
            throw new BizException("权益id为空");
        }

        List<Rights> rights = rightsService.listByIds(rightsIds);
        if(CollectionUtils.isEmpty(rights)){
            throw new BizException("权益不存在！");
        }
        List<CRightsVo> cRightsVos = new ArrayList<>(rights.size());

        for (Rights right : rights) {

            RightsVo rightsVo = RightsConvert.I.convertRights(right);
            CRightsVo cRightsVo = RightsConvert.I.convertCRights(rightsVo);
            cRightsVos.add(cRightsVo);
            if(rightsVo.getRule()==null){
                continue;
            }
            List<String> rules = rightsVo.getRule().getRule();
            Long everyUpdateNumber = rightsVo.getRule().getEveryUpdateNumber();

            List<String> ruleDesc = rules.stream().map(RightsRuleEnum::getDesc).collect(Collectors.toList());
            CRightsRuleVo cRightsRuleVo = new CRightsRuleVo();
            cRightsRuleVo.setRule(ruleDesc);
            if(rightsVo.getType().contains("BALANCE")){
                cRightsRuleVo.setEveryUpdateNumber(BalanceFormatUtil.getUserBalance(everyUpdateNumber));
            }else {
                cRightsRuleVo.setEveryUpdateNumber(String.valueOf(everyUpdateNumber));
            }
            cRightsVo.setRule(cRightsRuleVo);
        }
        return cRightsVos;
    }




}
