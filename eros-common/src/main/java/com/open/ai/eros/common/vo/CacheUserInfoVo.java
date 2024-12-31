package com.open.ai.eros.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *   此类是保存在 服务端缓存的类，不允许返回到 前端
 * </p>
 *
 * @since 2023-09-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheUserInfoVo {


      private Long id;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 用户名
     */
    private String userName;


    ///**
    // * 可提现金额
    // */
    //private String withDrawable;
    //
    ///**
    // * 不可提现金额
    // */
    //private String noWithDrawable;

    /**
     * 被邀请代码
     */
    private String invitedCode;

    /**
     * 邀请码
     */
    private String invitationCode;


    /**
     * 登录的token
     */
    private String token;

    /**
     * 用户角色
     */
    private String role;

}
