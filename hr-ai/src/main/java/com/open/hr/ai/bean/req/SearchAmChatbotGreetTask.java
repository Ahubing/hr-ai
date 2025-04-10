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
public class SearchAmChatbotGreetTask implements Serializable {


    /**
     * 任务id
     */
    private Integer id;

    /**
     * 账号id
     */
    @NotNull(message = "账号不能为空")
    private String accountId;

    /**
     * 任务类型。0每日任务，1临时任务，2复聊
     */
    @NotNull(message = "任务类型不能为空")
    private Integer taskType;

    /**
     * 执行时间
     */
    private String execTime;



}
