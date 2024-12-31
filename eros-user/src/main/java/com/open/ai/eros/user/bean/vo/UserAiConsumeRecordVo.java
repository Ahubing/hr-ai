package com.open.ai.eros.user.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户的ai消费记录
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-11
 */

@ApiModel("ai消费记录")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserAiConsumeRecordVo implements Serializable {

    private static final long serialVersionUID=1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 面具
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long maskId;

    /**
     * 面具名称
     */
    private String maskName;

    /**
     * 模型标识
     */
    private String model;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 输入的token
     */
    private Long promptToken;

    private Long chatId;

    /**
     * ai回答的消耗
     */
    private Long relyToken;

    /**
     * 消耗的额度（单位 美元）
     */
    private String cost;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;


    /**
     * 计费方式
     */
    private Integer costType;


    private String costTypeDesc;

}
