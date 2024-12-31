package com.open.ai.eros.db.mysql.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.UserExchangeCodeRecord;
import com.open.ai.eros.db.mysql.user.mapper.UserExchangeCodeRecordMapper;
import com.open.ai.eros.db.mysql.user.service.IUserExchangeCodeRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户兑换码记录表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-27
 */
@Service
public class UserExchangeCodeRecordServiceImpl extends ServiceImpl<UserExchangeCodeRecordMapper, UserExchangeCodeRecord> implements IUserExchangeCodeRecordService {




    public UserExchangeCodeRecord getExchangeCodeRecordByUserIdAndCode(Long userId, Long codeId){
        return this.getBaseMapper().getExchangeCodeRecordByUserIdAndCode(userId,codeId);
    }



    public boolean addRecord(Long exchangeCodeId,Long userId){
        UserExchangeCodeRecord record = new UserExchangeCodeRecord();
        record.setExchangeCodeId(exchangeCodeId);
        record.setUserId(userId);
        record.setCreateTime(LocalDateTime.now());
        return this.save(record);
    }


}
