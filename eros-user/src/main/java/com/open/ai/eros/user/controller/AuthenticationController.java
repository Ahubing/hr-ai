package com.open.ai.eros.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.privacy.utils.CryptoUtil;
import com.open.ai.eros.user.bean.req.AddUserInfoReq;
import com.open.ai.eros.user.bean.req.LoginReq;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.AuthenticationManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description：身份认证，主要功能包括登录、注册、第三方账号登录
 * @项目名：blue-cat-api
 * @创建人：Administrator
 * @创建时间：2024/8/4 9:30
 */
@Slf4j
@RestController
@Api(tags = "身份认证")
public class AuthenticationController extends UserBaseController {

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private RedisClient redisClient;

    @Value("${project.name}")
    private String projectName;

    private Set<String> testAccountSet = new HashSet<>(Arrays.asList("vip-user@gmail.com","vip-creator@gmail.com","vip-system@gmail.com","svip-plus@gmail.com"));


    /**
     * 用户注册
     *
     * @param req
     * @return
     */
    @PostMapping("/user/register")
    @ApiOperation(value = "用户注册",notes = "用户注册", httpMethod = "POST", response = ResultVO.class)
    public ResultVO register(@RequestBody @Valid AddUserInfoReq req){

        try {
            if(!req.getAgreeProtocol()){
                return ResultVO.fail("请同意先"+projectName+"使用协议!");
            }
            //if(!testAccountSet.contains(req.getEmail())){
            //    String emailKey = String.format(CommonConstant.CODE_KEY, req.getEmail());
            //    String code = redisClient.get(emailKey);
            //    if(code==null){
            //        //验证码过期了
            //        return ResultVO.fail("该验证码已过期了,请重新获取验证码");
            //    }else if(!code.equals(req.getVerificationCode().toLowerCase())){
            //        return ResultVO.fail("验证码错误，请输入正确的验证码");
            //    }
            //    Long del = redisClient.del(emailKey);
            //    if(del<=0){
            //        return ResultVO.fail("服务器繁忙！");
            //    }
            //}
            String password = req.getPassword();
            req.setPassword(Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(password.getBytes("UTF-8"))));

            return authenticationManager.register(req);
        }catch (Exception e){
            log.error("register error  req={}",req,e);
        }
        return ResultVO.fail("注册失败!");
    }

    /**
     * 用户登录
     *
     * @param loginReq
     * @return
     */
    @PostMapping(value = "/user/login")
    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST", response = ResultVO.class)
    public ResultVO login(@Valid @RequestBody LoginReq loginReq) {
        try {
            log.info("用户登录，账号:{}", loginReq.getEmail());

            // 去除前后空格
            String email = loginReq.getEmail().trim();
            String password = loginReq.getPassword().trim();
            password = Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(password.getBytes("UTF-8")));

            // 调用身份验证业务类的登录方法
            return authenticationManager.login(email, password);

        }catch (Exception e){
            log.error("login error req={}", JSONObject.toJSONString(loginReq),e);
            return ResultVO.fail("服务器繁忙！请联系管理员");
        }
    }

    /**
     * 用户退出
     *
     * @param token 用户令牌
     * @return
     */
    @PostMapping(value = "/user/logout")
    @ApiOperation(value = "用户退出", notes = "用户退出登录", httpMethod = "POST", response = ResultVO.class)
    public ResultVO logout(@RequestHeader("Authorization") String token) {
        try {
            log.info("用户退出，token:{}", token);
            return authenticationManager.logout(token);
        } catch (Exception e) {
            log.error("logout error token={}", token, e);
            return ResultVO.fail("退出失败，请稍后重试");
        }
    }

    /**
     * 修改密码（忘记密码）
     * @param email 邮箱
     * @param verificationCode 验证码
     * @param newPassword 新密码
     * @return
     */
    @ApiOperation("忘记密码")
    @PostMapping("/user/forget-password")
    public ResultVO forgetPassword(@RequestParam String email,
                                   @RequestParam String newPassword,
                                   @RequestParam String verificationCode) {
        try {
            // 基本参数验证
            if (email == null || email.isEmpty()) {
                return ResultVO.fail("邮箱不允许为空");
            }
            if (verificationCode == null || verificationCode.isEmpty()) {
                return ResultVO.fail("验证码不允许为空");
            }
            if (newPassword == null || newPassword.isEmpty()) {
                return ResultVO.fail("新密码不允许为空");
            }

            String EncryptedNewPassword = Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(newPassword.getBytes("UTF-8")));

            // 调用服务层方法处理忘记密码逻辑
            return authenticationManager.forgetPassword(email, verificationCode, EncryptedNewPassword);
        } catch (Exception e){
            log.error("login error req={}", JSONObject.toJSONString(email),e);
            return ResultVO.fail("服务器繁忙！请联系管理员");
        }
    }
    /**
     * 后台用户登录
     *
     * @param loginReq
     * @return
     */
    @PostMapping(value = "admin/login")
    @ApiOperation(value = "后台用户登录", notes = "后台用户登录", httpMethod = "POST", response = ResultVO.class)
    public ResultVO adminLogin(@Valid @RequestBody LoginReq loginReq) {
        try {
            log.info("后台用户登录，账号:{}", loginReq.getEmail());

            // 去除前后空格
            String email = loginReq.getEmail().trim();
            String password = loginReq.getPassword().trim();
            password = Base64.getEncoder().encodeToString(CryptoUtil.encryptMD5(password.getBytes("UTF-8")));
            // 调用身份验证业务类的登录方法
            return authenticationManager.adminLogin(email, password);
        }catch (Exception e){
            log.error("login error req={}", JSONObject.toJSONString(loginReq),e);
            return ResultVO.fail("服务器繁忙！请联系管理员");
        }
    }





}
