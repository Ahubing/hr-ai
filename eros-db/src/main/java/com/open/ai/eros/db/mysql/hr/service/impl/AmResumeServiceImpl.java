package com.open.ai.eros.db.mysql.hr.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.db.event.ReviewTypeUpdatedEvent;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.mapper.AmResumeMapper;
import com.open.ai.eros.db.mysql.hr.service.IAmResumeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 简历 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Service
@RequiredArgsConstructor
public class AmResumeServiceImpl extends ServiceImpl<AmResumeMapper, AmResume> implements IAmResumeService {

    private static final Logger log = LoggerFactory.getLogger(AmResumeServiceImpl.class);

    private final ApplicationEventPublisher eventPublisher;

    public void updateType(AmResume amResume, Boolean isAlUpdate, ReviewStatusEnums newType){
        ReviewStatusEnums oldType = ReviewStatusEnums.getEnumByStatus(amResume.getType());
        amResume.updateType(newType, isAlUpdate);
        if(!oldType.equals(newType)){
            ReviewTypeUpdatedEvent updatedEvent = new ReviewTypeUpdatedEvent(amResume, oldType, newType);
            log.info("发布简历状态更新事件事件：{}", JSONObject.toJSONString(updatedEvent));
            eventPublisher.publishEvent(updatedEvent);
        }
    }
}
