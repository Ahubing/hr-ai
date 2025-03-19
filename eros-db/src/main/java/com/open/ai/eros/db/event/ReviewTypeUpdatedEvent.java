package com.open.ai.eros.db.event;

import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewTypeUpdatedEvent {
    private final AmResume amResume;            // 简历信息
    private final ReviewStatusEnums oldType;    // 原状态值
    private final ReviewStatusEnums newType;    // 新状态值
}
