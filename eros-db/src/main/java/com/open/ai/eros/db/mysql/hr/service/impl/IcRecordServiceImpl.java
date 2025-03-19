package com.open.ai.eros.db.mysql.hr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.constants.InterviewStatusEnum;
import com.open.ai.eros.db.mysql.hr.entity.IcRecord;
import com.open.ai.eros.db.mysql.hr.mapper.IcRecordMapper;
import com.open.ai.eros.db.mysql.hr.service.IIcRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试日历-预约记录 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-24
 */
@Service
public class IcRecordServiceImpl extends ServiceImpl<IcRecordMapper, IcRecord> implements IIcRecordService {


    @Override
    public IcRecord getOneNormalIcRecord(String employeeUid, Long adminId, String accountId, Serializable positionId) {
        return  getOne(new LambdaQueryWrapper<IcRecord>()
                .eq(IcRecord::getCancelStatus, InterviewStatusEnum.NOT_CANCEL.getStatus())
                .eq(IcRecord::getEmployeeUid, employeeUid)
                .eq(IcRecord::getAdminId, adminId)
                .eq(IcRecord::getAccountId, accountId)
                .ge(IcRecord::getStartTime, LocalDateTime.now())
                .eq(IcRecord::getPositionId, positionId),false);
    }
}
