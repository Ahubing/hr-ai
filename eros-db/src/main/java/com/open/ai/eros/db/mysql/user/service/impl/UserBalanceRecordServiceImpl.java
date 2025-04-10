package com.open.ai.eros.db.mysql.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.UserBalanceRecord;
import com.open.ai.eros.db.mysql.user.entity.UserIncomeStatVo;
import com.open.ai.eros.db.mysql.user.mapper.UserBalanceRecordMapper;
import com.open.ai.eros.db.mysql.user.service.IUserBalanceRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户余额的记录表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-14
 */
@Service
public class UserBalanceRecordServiceImpl extends ServiceImpl<UserBalanceRecordMapper, UserBalanceRecord> implements IUserBalanceRecordService {


    /**
     * 统计今天的
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public UserIncomeStatVo statUserTodayIncome(Long userId, List<String> types, Date startTime, Date endTime) {
        return this.getBaseMapper().statUserTodayIncome(userId, types, startTime, endTime);
    }


    /**
     * 按天分页统计
     *
     * @param startTime
     * @param endTime
     * @param pageSize
     * @return
     */
    public List<UserIncomeStatVo> statUserIncome(String type, Date startTime, Date endTime, Integer page, Integer pageSize) {
        return this.getBaseMapper().statUserIncome(type, startTime, endTime, (page - 1) * pageSize, pageSize);
    }


}
