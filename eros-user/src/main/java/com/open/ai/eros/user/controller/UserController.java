package com.open.ai.eros.user.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.util.SessionUser;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.req.SearchUserDetailReq;
import com.open.ai.eros.user.bean.req.SearchUserReq;
import com.open.ai.eros.user.bean.req.UserUpdateReq;
import com.open.ai.eros.user.bean.vo.SearchUserResponseVo;
import com.open.ai.eros.user.bean.vo.SimpleUserVo;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.convert.UserConvert;
import com.open.ai.eros.user.manager.UserManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @类名：UserController
 * @项目名：web-eros-ai
 * @description：用户信息控制类
 * @创建人：Administrator
 * @创建时间：2024/8/3 23:05
 */

@Api(tags = "用户信息控制类")
@RestController
@Slf4j
public class UserController extends UserBaseController {


    @Autowired
    private UserManager userManager;


    /**
     * 获取用户的基础信息
     * 1. 用户的基础信息
     * 2. 余额信息：1. 可提现积分 2.不可提现积分
     * @return
     */
    @ApiOperation(value = "用户基础信息")
    @VerifyUserToken
    @GetMapping("/info")
    public ResultVO<CacheUserInfoVo> getUserInfo(){
        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        if(cacheUserInfoVo!=null){
            CacheUserInfoVo newUser = UserConvert.I.convertCacheUserInfoVo(cacheUserInfoVo);
            newUser.setToken(null);
            return ResultVO.success(newUser);
        }
        return ResultVO.fail("获取用户信息错误");
    }


    /**
     * 获取邀请列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "获取邀请列表")
    @VerifyUserToken
    @GetMapping("/invited/list")
    public ResultVO<PageVO<SimpleUserVo>> getInvited(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize){

        String invitationCode = SessionUser.get().getInvitationCode();
        return userManager.getInvited(invitationCode,pageNum,pageSize);
    }


    /**
     * 管理人员搜索人员信息
     * @param searchUserReq
     * @return
     */
    @ApiOperation(value = "后台管理查询用户列表")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/user/search")
    public ResultVO<PageVO<SearchUserResponseVo>> searchUser(@RequestBody @Valid SearchUserReq searchUserReq){
        if (Objects.isNull(searchUserReq)){
            return ResultVO.fail("查询参数不能为空");
        }
        return userManager.searchUser(searchUserReq);
    }

    /**
     * 管理人员搜索人员信息
     * @return
     */
    @ApiOperation(value = "后台管理查询用户")
    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @GetMapping("/user/detail/search")
    public ResultVO<SearchUserResponseVo> searchUserById(){
        CacheUserInfoVo cacheUserInfo = getCacheUserInfo();
        if (Objects.isNull(cacheUserInfo)){
            return ResultVO.fail("查询参数不能为空");
        }
        SearchUserDetailReq searchUserReq = SearchUserDetailReq.builder().email(cacheUserInfo.getEmail()).build();
        return userManager.searchUserDetail(searchUserReq);
    }


    /**
     * 更新用户信息
     * @param userUpdateReq 用户更新请求参数类
     * @return
     */
    @VerifyUserToken
    @PostMapping("/update/info")
    @ApiOperation("更新用户信息")
    public ResultVO updateUserInfo(@RequestBody @Valid UserUpdateReq userUpdateReq) {
        try {
            if(RoleEnum.SYSTEM.getRole().equals(getRole()) && userUpdateReq.getId()==null){
                return ResultVO.fail("用户id不能为空");
            }else {
                userUpdateReq.setId(getUserId());
            }
            boolean updated = userManager.updateUserInfo(userUpdateReq);
            return updated ? ResultVO.success("个人信息更新成功") : ResultVO.fail("个人信息更新失败");
        } catch (Exception e) {
            log.error("更新用户信息时发生错误", e);
            return ResultVO.fail("更新用户信息时发生错误");
        }
    }



    /**
     * b端更新用户信息
     * @param userUpdateReq 用户更新请求参数类
     * @return
     */
    @VerifyUserToken
    @PostMapping("b/update/info")
    @ApiOperation("b端更新用户信息")
    public ResultVO updateUserInfoTB(@RequestBody @Valid UserUpdateReq userUpdateReq) {
        try {
            if(!RoleEnum.SYSTEM.getRole().equals(getRole()) ||  userUpdateReq.getId()==null){
                return ResultVO.fail("用户id不能为空");
            }
            boolean updated = userManager.updateUserInfo(userUpdateReq);
            return updated ? ResultVO.success("个人信息更新成功") : ResultVO.fail("个人信息更新失败");
        } catch (Exception e) {
            log.error("更新用户信息时发生错误", e);
            return ResultVO.fail("更新用户信息时发生错误");
        }
    }




    @VerifyUserToken
    @GetMapping("/simple")
    @ApiOperation("获取简单的用户信息")
    public ResultVO<SimpleUserVo> getSimpleUser(@RequestParam(value = "id") Long id){
        return userManager.getSimpleUser(id);
    }



    /**
     * 用于查询用户信息的列表
     * @return
     */
    @ApiOperation("用户信息的列表 ")
    @GetMapping("/user/list")
    @VerifyUserToken(role = RoleEnum.SYSTEM)
    public ResultVO<List<SimpleUserVo>> getUserList(){
        return userManager.getAllSimpleUser();
    }






}
