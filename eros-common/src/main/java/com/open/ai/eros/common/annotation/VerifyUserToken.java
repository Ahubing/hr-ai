package com.open.ai.eros.common.annotation;

import com.open.ai.eros.common.constants.RoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface VerifyUserToken {


    /**
     * 是否是必需要登录
     * true: 没登录，直接返回用户未登录
     * false：没登录，继续执行，userId 为空
     */
    boolean required() default true;


    RoleEnum[] role() default {};

}
