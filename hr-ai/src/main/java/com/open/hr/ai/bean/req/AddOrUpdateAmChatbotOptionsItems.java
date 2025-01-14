package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 * 方案选项
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddOrUpdateAmChatbotOptionsItems {


    private Integer id;

    /**
     * 关联的方案id
     */
    @NotNull(message = "关联的方案id不能为空")
    @ApiModelProperty(value = "关联的方案", required = true, notes = "关联的方案id")
    private Integer optionId;

    /**
     * 第几天
     */
    @ApiModelProperty(value = "第几天", required = true, notes = "第几天开始触发")
    private Integer dayNum;

    /**
     * 执行时间。根据day_num区分。如第一天30分钟,填1800，单位为秒；第二天则填09:00这类时分，定时执行
     */
    @NotNull(message = "执行时间不能为空")
    @ApiModelProperty(value = "执行时间。根据day_num区分", required = true, notes = "根据day_num区分。如第一天30分钟,填1800，单位为秒；第二天则填09:00这类时分，定时执行")
    private String execTime;

    /**
     * 回复类型。text表示文本回复，img表示图片回复，ai表示AI回复
     */
    @ApiModelProperty(value = "回复类型", required = false, notes = "text表示文本回复，img表示图片回复，ai表示AI回复")
    private String replyType;

    /**
     * ai角色，类型为ai时上传
     */
    @ApiModelProperty(value = "ai角色，类型为ai时上传", required = false, notes = "ai角色，类型为ai时上传")
    private String aiRole;

    /**
     * 话术内容
     */
    @NotNull(message = "话术内容不能为空")
    @ApiModelProperty(value = "话术内容", required = false, notes = "话术内容不能为空")
    private String content;

    /**
     * 追问内容
     */
    @ApiModelProperty(value = "追问内容", required = false, notes = "追问内容")
    private Object repeatContent;


}
