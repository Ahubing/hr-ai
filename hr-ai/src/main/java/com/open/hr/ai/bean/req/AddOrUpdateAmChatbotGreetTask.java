package com.open.hr.ai.bean.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

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

    private Integer id;

    /**
     * 账号id
     */
    @NotNull(message = "账号不能为空")
    private String accountId;

    /**
     * 职位id
     */
    @NotNull(message = "职位不能为空")
    private Integer positionId;

    /**
     * 任务类型。0每日任务，1临时任务，2复聊
     */
    private Integer taskType;

    /**
     * 执行时间
     */
    private String execTime;

    /**
     * 完成数量
     */
    private Integer doneNum;

    /**
     * 筛选条件id
     */
    private Integer conditionsId;

    /**
     * 计划任务数量
     */
    private Integer taskNum;

    /**
     * 任务状态，0未发送，1已发送，2已完成（脚本回复完成）3失败（重试3次仍未成功）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Integer createTime;

    /**
     * 更新时间
     */
    private Integer updateTime;


}
