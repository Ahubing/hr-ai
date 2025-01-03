package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 验证码
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_sms_code")
public class AmSmsCode implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "code_id", type = IdType.AUTO)
    private Integer codeId;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 注册,1：找回登录密码，2：找回支付密码
     */
    private Integer type;

    /**
     * 验证码
     */
    private String code;

    /**
     * 发送时间
     */
    private Integer sendTime;

    /**
     * 是否验证 1未验证 2已验证
     */
    private Integer status;

    /**
     * 哪一天的
     */
    private Integer day;


}
