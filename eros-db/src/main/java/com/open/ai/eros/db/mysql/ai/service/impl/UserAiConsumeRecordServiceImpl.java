package com.open.ai.eros.db.mysql.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecord;
import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecordStatVo;
import com.open.ai.eros.db.mysql.ai.entity.UserAiMasksRecordStatVo;
import com.open.ai.eros.db.mysql.ai.mapper.UserAiConsumeRecordMapper;
import com.open.ai.eros.db.mysql.ai.service.IUserAiConsumeRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户的ai消费记录 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-11
 */
@Service
public class UserAiConsumeRecordServiceImpl extends ServiceImpl<UserAiConsumeRecordMapper, UserAiConsumeRecord> implements IUserAiConsumeRecordService {


    /**
     * 统计今天的
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public UserAiConsumeRecordStatVo statTodayConsumeRecord(List<Long> maskIds, Date startTime, Date endTime) {
        return this.getBaseMapper().statTodayConsumeRecord(maskIds, startTime, endTime);
    }



    /**
     * 统计七天内的消费记录
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List<UserAiConsumeRecordStatVo> statWeekConsumeRecord(List<Long> maskIds, Date startTime, Date endTime) {
        return this.getBaseMapper().statWeekConsumeRecord(maskIds, startTime, endTime);
    }

    /**
     * 按天分页统计
     *
     * @param startTime
     * @param endTime
     * @param pageSize
     * @return
     */
    public List<UserAiConsumeRecordStatVo> statConsumeRecord(Date startTime, Date endTime, Integer page, Integer pageSize) {
        return this.getBaseMapper().statConsumeRecord(startTime, endTime, (page - 1) * pageSize, pageSize);
    }

    /**
     * 根据用户查询当天的所有消费面具
     *
     * @param startTime
     * @return
     */
    public List<UserAiConsumeRecordStatVo> todayStatConsumeRecordByUserId(Date startTime,Long userId) {
        return this.getBaseMapper().todayStatConsumeRecordByUserId(startTime, userId);
    }


    /**
     * 统计根据时间窗口统计
     *
     * @param startTime
     * @return
     */
    public UserAiMasksRecordStatVo statTodayConsumeRecord(List<Long> maskIds, Date startTime) {
       return this.getBaseMapper().statMasksConsumeRecord(maskIds, startTime);
    }

    /**
     * 统计今天的使用次数
     *
     * @param startTime
     * @return
     */
    public Long statMasksRecordToday( Date startTime) {
       return this.getBaseMapper().statMasksRecordToday( startTime);
    }

}
