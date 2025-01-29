package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.util.SessionUser;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.AmZpAccoutsResultVo;
import com.open.hr.ai.bean.AmZpPlatformsResultVo;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.AmZpManager;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @类名：amZpController
 * @项目名：ai-recruitment
 * @description：
 * @创建人：zhxm
 * @创建时间：2025/1/5 10:23
 */
@Slf4j
@RestController
public class AmZpController extends HrAIBaseController {

    @Autowired
    private AmZpManager amZpManager;

    @ApiOperation(value = "多账号登录列表", notes = "多账号登录列表", httpMethod = "GET", response = ResultVO.class)
    @VerifyUserToken
    @GetMapping("/zp/list")
    public ResultVO<AmZpAccoutsResultVo> list() {
        Long userId = getUserId();
        return amZpManager.getAccouts(userId);
    }


    @ApiOperation(value = "平台列表", notes = "平台列表", httpMethod = "GET", response = ResultVO.class)
    @VerifyUserToken
    @GetMapping("/zp/platforms")
    public ResultVO<AmZpPlatformsResultVo> platforms() {
        return amZpManager.getPlatforms();
    }


    @ApiOperation(value = "添加招聘平台(admin超管设置)", notes = "添加招聘平台(admin超管设置)", httpMethod = "POST", response = ResultVO.class)
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/zp/add_platform")
    public ResultVO addPlatform(@RequestBody @Valid AmZpPlatformAddReq req) {

        return amZpManager.addPlatform(req.getName());
    }

    @ApiOperation(value = "修改招聘平台名称", notes = "修改招聘平台名称", httpMethod = "POST", response = ResultVO.class)
    @VerifyUserToken
    @PostMapping("/zp/modify_platform")
    public ResultVO modifyPlatformName(@RequestBody @Valid AmZpPlatformModifyReq req) {

        Long id = req.getId();
        String name = req.getName();
        return amZpManager.modifyPlatformName(id, name);
    }


    @ApiOperation(value = "删除招聘平台", notes = "删除招聘平台", httpMethod = "POST", response = ResultVO.class)
    @VerifyUserToken
    @PostMapping("/zp/delete_platform")
    public ResultVO deletePlatformName(@RequestBody @Valid AmZpPlatformDelReq req) {
        Long id = req.getId();
        if (id <= 0) {
            return ResultVO.fail("参数有误：" + id);
        }
        return amZpManager.deletePlatformName(id);
    }


    @ApiOperation(value = "添加招聘账号", notes = "添加招聘账号", httpMethod = "POST", response = ResultVO.class)
    @VerifyUserToken
    @PostMapping("/zp/add_account")
    public ResultVO addAccount(@RequestBody @Valid AmZpAccountAddReq req) {

        Long uid = SessionUser.get().getId();//
        if (uid <= 0) {
            return ResultVO.fail("登录失效，请重新登录");
        }
        Long platform_id = req.getPlatformId();
        String account = req.getAccount();
        String city = req.getCity();
        if (platform_id <= 0 || isEmpty(account) || isEmpty(city)) {
            return ResultVO.fail("参数有误！");
        }
        return amZpManager.addAccount(uid, platform_id, account, city);
    }


    @ApiOperation(value = "删除招聘账号", notes = "删除招聘账号", httpMethod = "POST", response = ResultVO.class)
    @VerifyUserToken
    @PostMapping("/zp/delete_account")
    public ResultVO delAccount(@RequestBody @Valid AmZpAccountDelReq req) {
        String id = req.getId();
        if (isEmpty(id)) {
            return ResultVO.fail("id不能为空");
        }
        return amZpManager.delAccount(id);
    }


    @ApiOperation(value = "更新运行状态", notes = "更新运行状态", httpMethod = "POST", response = ResultVO.class)
    @VerifyUserToken
    @PostMapping("/zp/modify_running_status")
    public ResultVO modifyRunningStatus(@RequestBody @Valid AmZpAccountModifyStatusReq req) {
        String id = req.getId();
        int status = req.getIs_running() >= 1 ? 1 : 0;
        if (isEmpty(id)) {
            return ResultVO.fail("id不能为空");
        }
        return amZpManager.modifyRunningStatus(id, status);
    }

    @ApiOperation(value = "获取登录二维码", notes = "获取登录二维码", httpMethod = "GET", response = ResultVO.class)
    @GetMapping("/zp/get_login_qrcode")
    public ResultVO getLoginQrcode() {
        return amZpManager.getLoginQrcode(getUserId());
    }


    @ApiOperation(value = "退出登录", notes = "退出登录", httpMethod = "POST", response = ResultVO.class)
    @VerifyUserToken
    @PostMapping("/account_exit")
    public ResultVO accountExit() {
        CacheUserInfoVo cacheUserInfoVo = SessionUser.get();
        if (cacheUserInfoVo != null) {
            cacheUserInfoVo.setToken("");
            SessionUser.remove();
        }
        return ResultVO.success();
    }

}
