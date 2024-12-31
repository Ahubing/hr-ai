package com.open.ai.eros.db.mysql.ai.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAiMasksRecordStatVo {



    /**
     * 名下面具总使用记录条数
     */
    private Long recordCount;


    /**
     * 名下面具总使用人数
     */
    private Long usePeopleCount;

    /**
     * 面具总收入
     */
    private String cost;

}
