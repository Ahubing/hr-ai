package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
public class AmChatbotGreetTaskVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号id
     */
    private String accountId;

    /**
     * 职位id
     */
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
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime updateTime;

    private AmChatbotGreetConditionVo conditionVo;

    private List<AmChatbotGreetTaskVo> tasks;

    private Integer total;

    private AmPositionVo positionVo;


}
