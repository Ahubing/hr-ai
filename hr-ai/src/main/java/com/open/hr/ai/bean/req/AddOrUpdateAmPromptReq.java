package com.open.hr.ai.bean.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 提示词管理
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddOrUpdateAmPromptReq {


    private Integer id;

    /**
     * 名称
     */
    @NotEmpty(message = "名称不能为空")
    @ApiModelProperty(value = "名称", required = true, notes = "名称不能为空")
    private String name;

    /**
     * 模型
     */
    @NotEmpty(message = "模型不能为空")
    @ApiModelProperty(value = "模型", required = true, notes = "模型不能为空")
    private String model;

    /**
     * AI客服回复
     */
    @ApiModelProperty(value = "AI客服回复")
    private String prompt;

    /**
     * AI引导prompt
     */
    @ApiModelProperty(value = "AI引导prompt", required = false, notes = "AI引导prompt")
    private String prompt2;

    /**
     * 回复引导是否开启 类型.0html提示词，1为说明书AI生成提示词
     */
    @NotNull(message = "回复引导是否开启 类型不能为空")
    @ApiModelProperty(value = "回复引导是否开启", required = true, notes = "类型.0 html提示词，1为说明书AI生成提示词")
    private Integer type;

    /**
     * 跟进引导是否开启
     */
    @ApiModelProperty(value = "跟进引导是否开启", required = false, notes = "跟进引导是否开启")
    private Integer typeA;


    /**
     * 人员ID
     */
    @ApiModelProperty(value = "人员ID", required = false, notes = "人员ID")
    private Integer resumeId;

    /**
     * 简历存放地址
     */
    @ApiModelProperty(value = "简历存放地址", required = false, notes = "简历存放地址")
    private String url;

    /**
     * 是否处理过
     */
    @ApiModelProperty(value = "是否处理过", required = false, notes = "是否处理过")
    private Integer isRead;

    /**
     * 标签
     */
    @ApiModelProperty(value = "标签", required = false, notes = "标签")
    private String tags;


}
