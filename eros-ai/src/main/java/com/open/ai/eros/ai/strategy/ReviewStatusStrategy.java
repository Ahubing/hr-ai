package com.open.ai.eros.ai.strategy;

import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;

public interface ReviewStatusStrategy {

    /**
     * 是否支持处理该状态
     */
    boolean supports(ReviewStatusEnums statusEnums);

    /**
     * 执行处理逻辑
     */
    void handle(AmResume resume);
}
