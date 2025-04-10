package com.open.ai.eros.pay.order.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.OrderStatusEnum;
import com.open.ai.eros.pay.config.PayBaseController;
import com.open.ai.eros.pay.order.bean.req.CreateOrderReq;
import com.open.ai.eros.pay.order.bean.req.OrderSearchReq;
import com.open.ai.eros.pay.order.bean.vo.*;
import com.open.ai.eros.pay.order.manager.OrderManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @类名：OrderController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/13 0:55
 */

@Api(tags = "订单管理控制类")
@Slf4j
@RestController
public class OrderController extends PayBaseController {


    @Autowired
    private OrderManager orderManager;


    @VerifyUserToken
    @GetMapping("/order/c/list")
    @ApiOperation("c-查询订单")
    public ResultVO<OrderQuerySearchResult> list(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                                 @RequestParam(value = "status",required = false) Integer status){

        if(status!=null && !OrderStatusEnum.exist(status)){
            return ResultVO.fail("非法的状态");
        }
        return orderManager.list(pageNum,pageSize,getUserId(),status);
    }


    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/order/b/list")
    @ApiOperation("b-查询订单")
    public ResultVO<PageVO<OrderVo>> search(@RequestBody @Valid OrderSearchReq req){
        return  orderManager.orderList(req);
    }



    @ApiOperation("下订单")
    @VerifyUserToken
    @PostMapping("/create/order")
    public ResultVO<GetPayUrlVo> createOrder(@RequestBody @Valid CreateOrderReq req){
        return orderManager.createOrder(getUserId(),req.getGoodId(),req.getPayWay());
    }



    @ApiOperation("订单状态")
    @GetMapping("/order/status/list")
    public ResultVO<List<OrderStatusVo>> orderStatusList(){
        List<OrderStatusVo> orderStatusVos = new ArrayList<>();
        for (OrderStatusEnum value : OrderStatusEnum.values()) {
            OrderStatusVo orderStatusVo = new OrderStatusVo();
            orderStatusVo.setStatus(value.getStatus());
            orderStatusVo.setDesc(value.getDesc());
            orderStatusVos.add(orderStatusVo);
        }
        return ResultVO.success(orderStatusVos);
    }




}
