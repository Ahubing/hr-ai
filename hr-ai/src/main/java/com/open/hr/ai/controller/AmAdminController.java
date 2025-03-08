package com.open.hr.ai.controller;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.AmAdminRoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.privacy.utils.CryptoUtil;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.AmAdminRoleVo;
import com.open.hr.ai.bean.vo.AmAdminVo;
import com.open.hr.ai.bean.vo.SlackOffVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.AmAdminManager;
import com.open.hr.ai.manager.LoginManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @类名：AmAdminController
 * @项目名：ai-recruitment
 * @description：
 * @创建人：陈臣
 * @创建时间：2025/1/4 11:46
 */
@Slf4j
@Api(tags = "用户管理类")
@RestController
public class AmAdminController extends HrAIBaseController {

    @Autowired
    AmAdminManager amAdminManager;

    @Autowired
    LoginManager loginManager;


    @ApiOperation("查询用户列表")
    @VerifyUserToken
    @PostMapping("admin/list")
    public ResultVO<PageVO<AmAdminVo>> adminList(@RequestBody @Valid SearchAmAdminReq req) {
        Long adminId = getUserId();
        return amAdminManager.searchAdmin(req, adminId);
    }


    @ApiOperation("创建用户")
    @VerifyUserToken
    @PostMapping("admin/create")
    public ResultVO create(@RequestBody @Valid HrAddUserReq req) {
        Long adminId = getUserId();
        return amAdminManager.createUser(req, adminId);
    }

    /**
     * 删除账号
     * @param id
     * @return
     */
    @ApiOperation("删除用户")
    @VerifyUserToken
    @DeleteMapping("admin/delete")
    public ResultVO deleteAdmin(@RequestParam(value = "id", required = true) Long id) {
        Long adminId = getUserId();
        return amAdminManager.deleteUser(id, adminId);
    }

    @ApiOperation("禁用用户")
    @VerifyUserToken
    @DeleteMapping("admin/ban")
    public ResultVO banAdmin(@RequestParam(value = "id", required = true) Long id) {
        Long adminId = getUserId();
        return amAdminManager.banAdmin(id, adminId);
    }


    @ApiOperation("启用用户")
    @VerifyUserToken
    @GetMapping("admin/unban")
    public ResultVO unbanAdmin(@RequestParam(value = "id", required = true) Long id) {
        Long adminId = getUserId();
        return amAdminManager.unbanAdmin(id, adminId);
    }

    @ApiOperation("更新用户密码")
    @VerifyUserToken
    @PostMapping("admin/update/password")
    public ResultVO updateAdminPassWord(@RequestBody @Valid UpdateAmAdminPasswordReq req) {
        Long adminId = getUserId();
        if (StringUtils.isBlank(req.getPassword())){
            return ResultVO.fail("密码不能为空");
        }
        return amAdminManager.updatePassword(req, adminId);
    }

    @ApiOperation("更新用户角色")
    @VerifyUserToken
    @PostMapping("admin/update/role")
    public ResultVO updateAdminRole(@RequestBody @Valid UpdateAmAdminRoleReq req) {
        Long adminId = getUserId();
        return amAdminManager.updateRole(req, adminId);
    }

    @ApiOperation("更新用户信息")
    @VerifyUserToken
    @PostMapping("admin/update/info")
    public ResultVO updateAdminInfo(@RequestBody @Valid UpdateAmAdminInfoReq req) {
        Long adminId = getUserId();
        return amAdminManager.updateBaseInfo(req, adminId);
    }


    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST", response = ResultVO.class)
    @PostMapping("/login")
    public ResultVO login(@Valid @RequestBody HrLoginReq loginReq) {
        try {
            log.info("用户登录，账号:{}", loginReq.getUsername());

            // 去除前后空格
            String userName = loginReq.getUsername().trim();
            String password = loginReq.getPassword().trim();
            password = Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(password.getBytes("UTF-8")));

            // 调用身份验证业务类的登录方法
            return amAdminManager.login(userName, password);

        } catch (Exception e) {
            log.error("login error req={}", JSONObject.toJSONString(loginReq), e);
            return ResultVO.fail("服务器繁忙！请联系管理员");
        }
    }


    /**
     * 用户注册
     *
     * @param req
     * @return
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST", response = ResultVO.class)
    public ResultVO register(@RequestBody @Valid HrAddUserReq req) {
        try {

            String password = req.getPassword();
            req.setPassword(Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(password.getBytes("UTF-8"))));
            return amAdminManager.register(req);
        } catch (Exception e) {
            log.error("register error  req={}", req, e);
        }
        return ResultVO.fail("注册失败!");
    }


    @ApiOperation("获取账号角色")
    @VerifyUserToken
    @GetMapping("/admin/role")
    public ResultVO<List<AmAdminRoleVo>> adminRole() {
        List<AmAdminRoleVo> amAdminRoleVos = new ArrayList<>();
        for (AmAdminRoleEnum amAdminRoleEnum : AmAdminRoleEnum.values()) {
            if (amAdminRoleEnum == AmAdminRoleEnum.SYSTEM){
                continue;
            }
            AmAdminRoleVo amAdminRoleVo = new AmAdminRoleVo();
            amAdminRoleVo.setType(amAdminRoleEnum.getType());
            amAdminRoleVo.setDesc(amAdminRoleEnum.getDesc());
            amAdminRoleVos.add(amAdminRoleVo);
        }
        return ResultVO.success(amAdminRoleVos);
    }




    /**
     * 更新智能摸鱼
     *
     * @param req
     * @return
     */
    @PostMapping("/update/slack")
    @VerifyUserToken
    @ApiOperation(value = "更新智能摸鱼", notes = "更新智能摸鱼", httpMethod = "POST", response = ResultVO.class)
    public ResultVO updateSlack(@RequestBody @Valid SlackOffVo req) {
        try {
            return loginManager.updateSlack(req,getUserId());
        } catch (Exception e) {
            log.error("register error  req={}", req, e);
        }
        return ResultVO.fail("注册失败!");
    }


    /**
     * 获取登录账号
     * @return
     */
    @GetMapping("/getAdmin/slack")
    @VerifyUserToken
    @ApiOperation(value = "获取登录账号", notes = "获取登录账号", httpMethod = "GET", response = ResultVO.class)
    public ResultVO getAdmin() {
        try {
            return loginManager.getByToken(getUserId());
        } catch (Exception e) {
            log.error("getAdmin error id={}",getUserId(), e);
        }
        return ResultVO.fail("获取失败!");
    }

}
