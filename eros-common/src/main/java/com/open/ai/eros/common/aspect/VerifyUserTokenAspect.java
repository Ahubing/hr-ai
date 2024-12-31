package com.open.ai.eros.common.aspect;

import com.open.ai.eros.common.constants.BaseCodeEnum;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.service.LoginUserService;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.HttpUtil;
import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.common.util.WebUtils;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.util.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Aspect
@Component
@Order(10)
public class VerifyUserTokenAspect {

    @Qualifier("common")
    @Autowired
    private RedisClient redisClient;


    @Autowired
    private LoginUserService loginUserService;

    @Autowired
    private Environment environment;


    @Pointcut("@annotation(com.open.ai.eros.common.annotation.VerifyUserToken)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        VerifyUserToken userToken = getAnnotation(joinPoint, VerifyUserToken.class);
        boolean required = userToken.required();

        RoleEnum[] role = userToken.role();

        HttpServletRequest request = WebUtils.getRequest();
        //放行OPTIONS请求
        String method = request.getMethod();
        if("OPTIONS".equals(method)){
            return joinPoint.proceed();
        }

        CacheUserInfoVo userInfo = null;
        String token = request.getHeader(CommonConstant.USER_LOGIN_TOKEN);
        log.info("VerifyUserTokenAspect token ={},uri={}，ip={}", token ,request.getRequestURI(), HttpUtil.getIpAddress());
        if (!StringUtils.isEmpty(token)) {
            Map<String, String> redisCacheUserInfo = redisClient.hgetAll(String.format(CommonConstant.USER_LOGIN_TOKEN_KEY,token));
            if(!CollectionUtils.isEmpty(redisCacheUserInfo)){
                userInfo = new CacheUserInfoVo();
                ObjectToHashMapConverter.setValuesToObject(redisCacheUserInfo,userInfo);
            }else{
                String activeProfile = environment.getActiveProfiles()[0];
                if("dev".equals(activeProfile)){
                    // 测试环境 token就是 账号
                    userInfo = loginUserService.getCacheUserInfoVo(token);
                    if(userInfo == null){
                        // 获取不到，用户未登录
                        log.warn("user not login, token={}", token);
                        return ResultVO.fail(BaseCodeEnum.LOGIN_EXPIRE);
                    }
                    userInfo.setToken(token);
                }
            }
        }

        if (userInfo == null && required) {
            // 获取不到，用户未登录
            log.warn("user not login, token={}", token);
            return ResultVO.fail(BaseCodeEnum.LOGIN_EXPIRE);
        }
        if(userInfo!=null){
            if(role.length > 0){
                boolean anyMatch = Arrays.asList(role).contains(RoleEnum.getByRole(userInfo.getRole()));
                if(!anyMatch){
                    return ResultVO.fail("没有该接口访问权限！");
                }
            }
            // 设置进上下文中
            SessionUser.setSessionUserInfo(userInfo);
        }
        Object proceed = joinPoint.proceed();
        //一次请求后需要删除线程变量，否则会造成内存泄漏
        if(userInfo!=null){
            SessionUser.remove();
        }
        return proceed;
    }

    /**
     * 获取切入目标上指定的Annotation。此方法优先从方法获取，若不存在则从类获取
     *
     * @param pjp
     * @param annotationClass
     * @return
     */
    protected <T extends Annotation> T getAnnotation(final ProceedingJoinPoint pjp, Class<T> annotationClass) {
        Method targetMethod = getTargetMethod(pjp);
        T annotation = targetMethod.getAnnotation(annotationClass);
        if (annotation == null) {
            annotation = targetMethod.getDeclaringClass().getAnnotation(annotationClass);
        }
        return annotation;
    }

    /**
     * 获取切入目标方法
     *
     * @return
     */
    protected Method getTargetMethod(final ProceedingJoinPoint pjp) {
        return ((MethodSignature) pjp.getSignature()).getMethod();
    }


}
