package com.open.ai.eros.user.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用于返回后台的用户数据实体
 * @since 2023-09-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserResponseVo {


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


    /**
     * 可提现金额
     */
    private String withDrawable;

    /**
     * 不可提现金额
     */
    private String noWithDrawable;

    /**
     * 被邀请代码
     */
    private String invitedCode;

    /**
     * 邀请码
     */
    private String invitationCode;

    /**
     * 用户角色
     */
    private String role;


    /**
     * 谷歌账号ID，普通注册用户可为空
     */
    private String googleId;


    /**
     * 用户状态，ACTIVE:正常，DISABLED:禁用
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime updatedAt;

    /**
     * 注册天数
     */
    private Long  registerDay;

}
