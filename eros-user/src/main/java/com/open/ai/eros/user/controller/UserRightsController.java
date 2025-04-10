package com.open.ai.eros.user.controller;


import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.req.AddUserRightsReq;
import com.open.ai.eros.user.bean.vo.UserRightsVo;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.UserRightsManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Api(tags = "用户权益控制类")
@RestController
public class UserRightsController extends UserBaseController {

    @Autowired
    private UserRightsManager userRightsManager;


    /**
     * 获取 自己权益列表
     *
     * @return
     */
    @ApiOperation("获取自己权益")
    @VerifyUserToken
    @GetMapping("/user/rights/get")
    public ResultVO<List<UserRightsVo>> getMyRights(@RequestParam(value = "status",required = false,defaultValue = "1") Integer status){
        return userRightsManager.getMyRights(getUserId(), status);
    }




    @ApiOperation("用户添加权益")
    @VerifyUserToken(role = RoleEnum.SYSTEM)
    @PostMapping("/user/rights/add")
    public ResultVO addUserRights(@RequestBody @Valid AddUserRightsReq req){
        return userRightsManager.addUserRights(req.getUserId(),req.getRightsId());
    }


    @ApiOperation("用户刷新权益")
    @VerifyUserToken
    @GetMapping("/user/refresh/rights")
    public ResultVO refreshRights(){
        return userRightsManager.refreshRights(getUserId());
    }



    @ApiOperation("删除用户权益")
    @VerifyUserToken(role = RoleEnum.SYSTEM)
    @GetMapping("/user/delete/rights")
    public ResultVO deleteRights(@RequestParam(value = "id",required = true) Long userId,@RequestParam(value = "rightsId",required = true) Long rightsId){
        return userRightsManager.deleteRights(userId,rightsId);
    }






}

