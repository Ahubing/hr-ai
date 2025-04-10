package com.open.ai.eros.user.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.req.UpdateUserBalanceReq;
import com.open.ai.eros.user.bean.vo.UserBalanceVo;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.UserBalanceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @类名：UserBalanceController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/18 17:30
 */
@Api(tags = "用户余额表控制类")
@Slf4j
@RestController
public class UserBalanceController extends UserBaseController {


    @Autowired
    private UserBalanceManager userBalanceManager;


    @ApiOperation("获取用户的余额信息")
    @GetMapping("/user/balance")
    @VerifyUserToken
    public ResultVO<UserBalanceVo> getUserBalance(){
        UserBalanceVo userBalanceVo =  userBalanceManager.getUserBalance(getUserId());
        return ResultVO.success(userBalanceVo);
    }



    @ApiOperation("修改用户的余额信息")
    @PostMapping("/user/update/balance")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    public ResultVO updateUserBalance(@RequestBody @Valid UpdateUserBalanceReq req){
        return userBalanceManager.updateUserBalance(req);
    }



}
