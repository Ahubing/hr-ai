package com.open.ai.eros.social.email.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.util.RegexUtil;
import com.open.ai.eros.common.util.SessionUser;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.social.config.SocialBaseController;
import com.open.ai.eros.social.email.manager.MailServiceManager;
import com.open.ai.eros.social.email.bean.ToEmailDto;
import com.open.ai.eros.social.email.bean.req.NoLoginUserSendVerificationReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * 验证码服务类
 *
 */
@Api(tags = "验证码服务类")
@Slf4j
@RestController
public class EmailCodeController extends SocialBaseController {

    @Autowired
    private MailServiceManager mailServiceManager;

    /**
     * 非登录状态
     * 发送邮箱验证码/user/register/send/verification
     */
    @ApiOperation(value = "非登录状态 - 发送邮箱验证码")
    @PostMapping("/code/send/verification")
    public ResultVO<Object> sendEmailVerCode(@RequestBody @Valid NoLoginUserSendVerificationReq verificationReq){
        log.info("verificationReq verificationReq = {}",verificationReq);
        String account = verificationReq.getEmail();
        if(RegexUtil.validateEmail(account)){
            Boolean aBoolean = mailServiceManager.sendEmailVerCode(ToEmailDto.builder().tos(account).build());
            return ResultVO.success(aBoolean);
        }else {
            return ResultVO.fail("格式不正确");
        }
    }


    /**
     * 登录状态
     * 发送邮箱验证码
     */
    @ApiOperation(value = "登录状态 - 发送邮箱验证码")
    @VerifyUserToken
    @GetMapping("/code/user/send/verification")
    public ResultVO<Object> sendEmailCode(){
        String email = SessionUser.get().getEmail();
        if(StringUtils.isEmpty(email)){
            return ResultVO.fail("登录邮箱为空");
        }
        Boolean aBoolean = mailServiceManager.sendEmailVerCode(ToEmailDto.builder().tos(email).build());
        return ResultVO.success(aBoolean);
    }

}
