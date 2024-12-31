package com.open.ai.eros.db.mysql.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.Feedback;
import com.open.ai.eros.db.mysql.user.mapper.FeedbackMapper;
import com.open.ai.eros.db.mysql.user.service.IFeedbackService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 意见反馈表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {
}
