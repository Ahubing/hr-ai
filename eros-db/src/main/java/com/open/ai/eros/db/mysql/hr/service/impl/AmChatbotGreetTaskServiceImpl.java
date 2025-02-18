package com.open.ai.eros.db.mysql.hr.service.impl;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotGreetTask;
import com.open.ai.eros.db.mysql.hr.mapper.AmChatbotGreetTaskMapper;
import com.open.ai.eros.db.mysql.hr.service.IAmChatbotGreetTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 打招呼任务 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Service
public class AmChatbotGreetTaskServiceImpl extends ServiceImpl<AmChatbotGreetTaskMapper, AmChatbotGreetTask> implements IAmChatbotGreetTaskService {


    // 根据accountId 和 positionId 删除任务
    public Integer deleteByAccountIdAndPositionId(String accountId, Integer positionId) {
       return baseMapper.deleteByAccountIdAndPositionId(accountId, positionId);
    }
}
