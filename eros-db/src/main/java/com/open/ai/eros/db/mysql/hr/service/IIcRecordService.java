package com.open.ai.eros.db.mysql.hr.service;

import com.open.ai.eros.db.mysql.hr.entity.IcRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;

/**
 * <p>
 * 面试日历-预约记录 服务类
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-24
 */
public interface IIcRecordService extends IService<IcRecord> {

    IcRecord getOneNormalIcRecord(String employeeUid, Long adminId, String accountId, Serializable positionId);
}
