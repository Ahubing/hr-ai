package com.open.ai.eros.db.mysql.hr.service.impl;

import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.db.event.ReviewTypeUpdatedEvent;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.mapper.AmResumeMapper;
import com.open.ai.eros.db.mysql.hr.service.IAmResumeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
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

    private final ApplicationEventPublisher eventPublisher;

    public void updateType(AmResume amResume, Boolean isAlUpdate, ReviewStatusEnums newType){
        ReviewStatusEnums oldType = ReviewStatusEnums.getEnumByStatus(amResume.getType());
        amResume.updateType(newType, isAlUpdate);
        eventPublisher.publishEvent(new ReviewTypeUpdatedEvent(amResume, oldType, newType));
    }
}
