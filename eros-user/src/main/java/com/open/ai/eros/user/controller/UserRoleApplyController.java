package com.open.ai.eros.user.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.req.UserApplyCreatorReq;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.UserRoleApplyManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "用户角色申请控制类")
@RestController
@Slf4j
public class UserRoleApplyController extends UserBaseController {


    @Autowired
    private UserRoleApplyManager userRoleApplyManager;


    /**
     * 用户申请创作者权限
     *
     * @return
     */
    @ApiOperation("用户申请创作者权限")
    @VerifyUserToken(role = {RoleEnum.COMMON})
    @PostMapping("/apply/creator/role")
    public ResultVO applyCreatorRole(@RequestBody @Valid UserApplyCreatorReq req){
        return userRoleApplyManager.applyCreatorRole(getUserId(),req);
    }



    @ApiOperation("管理员审批创作者权限")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/approval/creator/role")
    public ResultVO approvalCreator(@RequestParam(value = "userId") Long userId){
        return userRoleApplyManager.approvalCreator(getAccount(), userId);
    }







}
