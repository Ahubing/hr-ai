package com.open.ai.eros.user.controller;


import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.req.GetUserIncomeStatDayReq;
import com.open.ai.eros.user.bean.vo.UserIncomeStatDayVo;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.UserIncomeStatManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Api(tags = "用户收益统计控制类")
@Slf4j
@RestController
public class UserIncomeStatController extends UserBaseController {


    @Autowired
    private UserIncomeStatManager userIncomeStatManager;


    @ApiOperation(value = "获取今日收益统计记录")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    @GetMapping("/income/stat/today")
    public ResultVO<UserIncomeStatDayVo> getUserIncomeStatToday(@RequestParam(value = "userId", required = false) Long userId) {
        if (!getRole().equals(RoleEnum.SYSTEM.getRole())) {
            userId = getUserId();
        }
        return userIncomeStatManager.getUserIncomeStatToday(userId);
    }


    @ApiOperation(value = "获取日收益统计记录")
    @VerifyUserToken(role = {RoleEnum.SYSTEM, RoleEnum.CREATOR})
    @GetMapping("/income/stat/day")
    public ResultVO<PageVO<UserIncomeStatDayVo>> getUserIncomeStatday(@Valid GetUserIncomeStatDayReq req) {
        if (!getRole().equals(RoleEnum.SYSTEM.getRole())) {
            req.setUserId(getUserId());
        }
        return userIncomeStatManager.getDailyStats(req);
    }


}
