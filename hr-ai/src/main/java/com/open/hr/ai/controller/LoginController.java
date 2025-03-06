package com.open.hr.ai.controller;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.privacy.utils.CryptoUtil;
import com.open.ai.eros.user.bean.req.AddUserInfoReq;
import com.open.hr.ai.bean.req.HrAddUserReq;
import com.open.hr.ai.bean.req.HrLoginReq;
import com.open.hr.ai.bean.vo.SlackOffVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.LoginManager;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Base64;

/**
 * @类名：LoginController
 * @项目名：ai-recruitment
 * @description：
 * @创建人：陈臣
 * @创建时间：2025/1/4 11:46
 */
@Slf4j
@RestController
public class LoginController extends HrAIBaseController {

    @Autowired
    LoginManager loginManager;


    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST", response = ResultVO.class)
//    @PostMapping("/login")
    public ResultVO login(@Valid @RequestBody HrLoginReq loginReq) {
        try {
            log.info("用户登录，账号:{}", loginReq.getUsername());

            // 去除前后空格
            String userName = loginReq.getUsername().trim();
            String password = loginReq.getPassword().trim();
            password = Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(password.getBytes("UTF-8")));

            // 调用身份验证业务类的登录方法
            return loginManager.login(userName, password);

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
//    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST", response = ResultVO.class)
    public ResultVO register(@RequestBody @Valid HrAddUserReq req) {
        try {

            String password = req.getPassword();
            req.setPassword(Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(password.getBytes("UTF-8"))));

            return loginManager.register(req);
        } catch (Exception e) {
            log.error("register error  req={}", req, e);
        }
        return ResultVO.fail("注册失败!");
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
