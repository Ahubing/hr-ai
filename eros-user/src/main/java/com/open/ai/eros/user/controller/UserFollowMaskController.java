package com.open.ai.eros.user.controller;


import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.vo.CMaskVo;
import com.open.ai.eros.user.bean.req.FollowMaskOpReq;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.UserFollowMaskManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "面具关注控制类")
@RestController
public class UserFollowMaskController extends UserBaseController {


    @Autowired
    private UserFollowMaskManager userFollowMaskManager;


    /**
     * 用户关注面具
     *
     * @return
     */
    @ApiOperation("用户关注面具操作")
    @VerifyUserToken
    @PostMapping("/user/follow/mask")
    public ResultVO followMask(@RequestBody @Valid FollowMaskOpReq req){
        return userFollowMaskManager.followMask(getUserId(),req);
    }



    /**
     * 用户关注面具列表
     *
     * @return
     */
    @ApiOperation("关注面具列表")
    @VerifyUserToken
    @GetMapping("/user/follow/mask/list")
    public ResultVO<List<CMaskVo>> followMaskList(){
        return userFollowMaskManager.getFollowList(getUserId());
    }







}
