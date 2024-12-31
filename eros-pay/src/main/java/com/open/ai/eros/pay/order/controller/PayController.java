package com.open.ai.eros.pay.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.OrderStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.Order;
import com.open.ai.eros.db.mysql.pay.service.impl.OrderServiceImpl;
import com.open.ai.eros.pay.config.PayBaseController;
import com.open.ai.eros.pay.config.PayConfig;
import com.open.ai.eros.pay.order.bean.dto.OrderOutNotifyDto;
import com.open.ai.eros.pay.order.manager.OrderManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类名：PayController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 21:02
 */

@Slf4j
@Api(tags = "支付相关控制类")
@RestController
public class PayController extends PayBaseController {


    @Autowired
    private PayConfig payConfig;

    @Autowired
    private OrderManager orderManager;

    @Autowired
    private OrderServiceImpl orderService;


    @ApiOperation("获取支付方式")
    @VerifyUserToken
    @GetMapping("/way")
    public ResultVO<List<String>> getPayWay(){
        String way = payConfig.getWay();
        String[] split = way.split(",");
        List<String> ways = Arrays.asList(split);
        return ResultVO.success(ways);
    }


    /**
     * 第三方
     *
     * 通知订单接口
     *
     * @return
     */
    @ApiOperation("第三方平台回调 支付接口成功")
    @GetMapping("/notify")
    public String notifyPayResult(@Valid OrderOutNotifyDto dto){
        log.info("notifyPayResult dto={}", JSONObject.toJSONString(dto));
        String tradeStatus = dto.getTrade_status();
        if("TRADE_SUCCESS".equals(tradeStatus)){
            orderService.updateOrderStatusToPaySuccess(Long.parseLong(dto.getOut_trade_no()));
        }else{
            orderService.updateOrderStatusToPayFail(Long.parseLong(dto.getOut_trade_no()));
        }
        return "success";
    }



    /**
     * 前端
     *
     * 通知订单接口
     *
     * @return
     */
    @ApiOperation("前端回调 支付接口成功")
    @VerifyUserToken
    @GetMapping("/complete")
    public ResultVO completePay(@RequestParam("orderId") Long orderId){
        log.info("completePay orderId={}",orderId);
        orderService.updateOrderStatusToPaySuccess(orderId);
        return ResultVO.success();
    }


    /**
     * 支付状态查询
     *
     * @param code
     * @return
     */
    @ApiOperation("根据流水号-支付状态查询")
    @VerifyUserToken
    @PostMapping("/status/code")
    public ResultVO<Map<String,Integer>> getOrderStatusByCode(@RequestParam(value = "code",required = true) String code){

        Order order = orderService.getOrderByCode(getUserId(), code);
        if(order==null){
            return ResultVO.fail("订单不存在");
        }
        Map<String,Integer> map = new HashMap<>();
        map.put("status",order.getStatus());
        return ResultVO.success(map);
    }



    /**
     * 支付状态查询
     * @param orderId
     * @return
     */
    @ApiOperation("根据订单-支付状态查询")
    @VerifyUserToken
    @GetMapping("/status/id")
    public ResultVO<Map<String,String>> getOrderStatusById(@RequestParam(value = "orderId",required = true) Long orderId){

        Order order = orderService.getOrderById(getUserId(), orderId);
        if(order==null){
            return ResultVO.fail("订单不存在");
        }
        Map<String,String> map = new HashMap<>();
        map.put("status",String.valueOf(order.getStatus()));
        map.put("statusDesc", OrderStatusEnum.getDesc(order.getStatus()));
        return ResultVO.success(map);
    }


}
