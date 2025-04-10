package com.open.ai.eros.social.email.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 邮箱验证码实体类
 */
@Data
@AllArgsConstructor //生成一个包含所有类字段的构造函数
@Builder
public class ToEmailDto implements Serializable {
    /**
     *  邮件接受方
     */
    private String tos;
    /**
     *      邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;
}
