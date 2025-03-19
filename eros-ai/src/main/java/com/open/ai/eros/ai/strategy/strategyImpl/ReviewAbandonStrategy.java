package com.open.ai.eros.ai.strategy.strategyImpl;

import com.open.ai.eros.common.constants.InterviewRoleEnum;
import com.open.ai.eros.ai.manager.ICAiManager;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.ai.strategy.ReviewStatusStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 状态变不合适
 */
@Component
public class ReviewAbandonStrategy implements ReviewStatusStrategy {

    @Resource
    private ICAiManager icAiManager;

    @Override
    public boolean supports(ReviewStatusEnums statusEnums) {
        return ReviewStatusEnums.ABANDON.equals(statusEnums);
    }

    @Override
    public void handle(AmResume resume) {
        //取消面试
        icAiManager.cancelInterview(resume.getUid(), InterviewRoleEnum.EMPLOYER.getCode(), false);
    }
}
