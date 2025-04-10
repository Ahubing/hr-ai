package com.open.ai.eros.db.mysql.ai.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAiConsumeRecordStatVo {

    /**
     * 统计的天 格式 yyyyMMdd
     */
    private LocalDateTime statsDay;

    /**
     * 面具作者ID
     */
    private Long userId;

    /**
     * 面具id
     */
    private Long maskId;

    /**
     * 消费ai额度 单位美元
     */
    private Long cost;

    /**
     * 记录条数
     */
    private Long recordCount;

}
