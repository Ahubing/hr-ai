package com.open.ai.eros.ai.strategy.strategyImpl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.constants.InterviewRoleEnum;
import com.open.ai.eros.ai.manager.ICAiManager;
import com.open.ai.eros.common.constants.InterviewStatusEnum;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.ai.strategy.ReviewStatusStrategy;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.entity.IcRecord;
import com.open.ai.eros.db.mysql.hr.service.impl.AmZpLocalAccoutsServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.IcRecordServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 状态变不合适
 */
@Component
public class ReviewAbandonStrategy implements ReviewStatusStrategy {

    private static final Logger log = LoggerFactory.getLogger(ReviewAbandonStrategy.class);

    @Resource
    private AmZpLocalAccoutsServiceImpl accoutsService;

    @Resource
    private IcRecordServiceImpl recordService;

    @Override
    public boolean supports(ReviewStatusEnums statusEnums) {
        return ReviewStatusEnums.ABANDON.equals(statusEnums);
    }

    @Override
    public void handle(AmResume resume) {
        //获取面试
        IcRecord record = recordService.getOneNormalIcRecord(resume.getUid(), resume.getAdminId(), resume.getAccountId(), resume.getPostId());
        log.info("查询到的面试为：{}", JSONObject.toJSONString(record));
        if(record == null){
            return;
        }
        //发送取消面试消息
        AmZpLocalAccouts account = accoutsService.getById(record.getAccountId());
        SendMessageUtil.generateAsyncMessage(resume,account,record, "cancel");
    }
}
