package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 打招呼任务
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddOrUpdateAmChatbotGreetTask implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "打招呼主键ID", required = false, notes = "修改时必传")
    private Integer id;

    /**
     * 账号id
     */
    @NotNull(message = "账号Id不能为空")
    @ApiModelProperty(value = "账号Id", required = false, notes = "账号Id不能为空")
    private String accountId;

    /**
     * 职位id
     */
    @NotNull(message = "职位不能为空")
    @ApiModelProperty(value = "职位id", required = false, notes = "账号Id不能为空")
    private Integer positionId;

    /**
     * 任务类型。0每日任务，1临时任务，2复聊
     */
    @ApiModelProperty(value = "任务类型", required = false, notes = "0每日任务，1临时任务，2复聊")
    private Integer taskType;

    /**
     * 执行时间
     */
    @ApiModelProperty(value = " 执行时间", required = false, notes = " 执行时间")
    private String execTime;

    /**
     * 完成数量
     */
    @ApiModelProperty(value = " 完成数量", required = false, notes = "完成数量")
    private Integer doneNum;

    /**
     * 筛选条件id
     */
    @ApiModelProperty(value = " 筛选条件id", required = false, notes = "筛选条件")
    private Integer conditionsId;

    /**
     * 计划任务数量
     */
    @ApiModelProperty(value = " 计划任务数量", required = false, notes = "计划任务数量")
    private Integer taskNum;

    /**
     * 任务状态，0未发送，1已发送，2已完成（脚本回复完成）3失败（重试3次仍未成功）
     */
    @ApiModelProperty(value = " 任务状态", required = false, notes = "0未发送，1已发送，2已完成（脚本回复完成）3失败（重试3次仍未成功）")
    private Integer status;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", required = false, notes = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", required = false, notes = "更新时间")
    private LocalDateTime updateTime;


}
