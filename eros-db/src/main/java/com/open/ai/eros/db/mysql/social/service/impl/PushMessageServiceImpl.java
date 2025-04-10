package com.open.ai.eros.db.mysql.social.service.impl;

import com.open.ai.eros.db.mysql.social.entity.PushMessage;
import com.open.ai.eros.db.mysql.social.mapper.PushMessageMapper;
import com.open.ai.eros.db.mysql.social.service.IPushMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */
@Service
public class PushMessageServiceImpl extends ServiceImpl<PushMessageMapper, PushMessage> implements IPushMessageService {


    public PushMessage getUserLastMessage(long userId, String source) {
        return this.baseMapper.getUserLastMessage(userId, source);
    }

}
