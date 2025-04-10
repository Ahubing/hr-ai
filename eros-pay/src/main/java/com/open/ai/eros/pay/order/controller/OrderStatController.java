package com.open.ai.eros.pay.order.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.OrderStatusEnum;
import com.open.ai.eros.pay.config.PayBaseController;
import com.open.ai.eros.pay.order.bean.req.CreateOrderReq;
import com.open.ai.eros.pay.order.bean.req.OrderSearchReq;
import com.open.ai.eros.pay.order.bean.vo.GetPayUrlVo;
import com.open.ai.eros.pay.order.bean.vo.OrderQuerySearchResult;
import com.open.ai.eros.pay.order.bean.vo.OrderStatusVo;
import com.open.ai.eros.pay.order.bean.vo.OrderVo;
import com.open.ai.eros.pay.order.manager.OrderManager;
import com.open.ai.eros.pay.order.manager.OrderStatDayManager;
import com.open.ai.eros.pay.vo.AllOrderStatVo;
import com.open.ai.eros.pay.vo.OrderStatDayVo;
import com.open.ai.eros.pay.vo.OrderStatListSearchVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @类名：OrderStatController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/13 0:55
 */

@Api(tags = "订单统计控制类")
@Slf4j
@RestController
public class OrderStatController extends PayBaseController {


    @Autowired
    private OrderStatDayManager orderStatDayManager;

    /**
     * 包含历史订单收入、今日订单收入,总收入
     * @return
     */
    @ApiOperation(value = "获取订单统计记录")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/order/stat/all")
    public ResultVO<AllOrderStatVo> getAllOrderIncome() {
        return orderStatDayManager.getAllOrderIncome();
    }


    @ApiOperation(value = "获取订单一周统计记录")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/order/stat/week")
    public ResultVO<List<OrderStatDayVo>> getOrderStatWeek() {
        return orderStatDayManager.getWeekOrderStatDay();
    }

    @ApiOperation(value = "分页获取订单统计记录")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/order/stat/list")
    public ResultVO<PageVO<OrderStatDayVo>> getOrderStatList(@RequestBody @Valid OrderStatListSearchVo orderStatListSearchVo) {
        return orderStatDayManager.getWeekOrderStatDay(orderStatListSearchVo);
    }

//
//    @ApiOperation(value = "获取创作者面具相关信息")
//    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
//    @PostMapping("/mask/info/stat")
//    public ResultVO<MasksInfoVo> getMasksInfo(@RequestBody @Valid MasksInfoSearchVo masksInfoSearchVo) {
//        Long userId = masksInfoSearchVo.getUserId();
//        if(!getRole().equals(RoleEnum.SYSTEM.getRole()) || Objects.isNull(userId)){
//            userId = getUserId();
//        }
//        return maskStatDayManager.getPeopleMasksInfo(userId, masksInfoSearchVo.getTimeWindow());
//    }
//
//    @ApiOperation(value = "获取创作者面具相关信息")
//    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
//    @PostMapping("/mask/stat/list")
//    public ResultVO<List<MaskStatListVo>> getMasksInfoList(@RequestBody @Valid MasksInfoSearchVo masksInfoSearchVo) {
//        Long userId = masksInfoSearchVo.getUserId();
//        if(!getRole().equals(RoleEnum.SYSTEM.getRole()) || Objects.isNull(userId)){
//            userId = getUserId();
//        }
//        return maskStatDayManager.getPeopleMasksStatList(userId, masksInfoSearchVo.getTimeWindow());
//    }


}
