package com.open.ai.eros.db.mysql.hr.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import com.open.ai.eros.db.mysql.hr.mapper.AmZpLocalAccoutsMapper;
import com.open.ai.eros.db.mysql.hr.service.IAmZpLocalAccoutsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 招聘本地账户 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Service
public class AmZpLocalAccoutsServiceImpl extends ServiceImpl<AmZpLocalAccoutsMapper, AmZpLocalAccouts> implements IAmZpLocalAccoutsService {

    @Override
    public List<AmZpLocalAccouts> getList(Long id) {
        return baseMapper.getList(id);
    }

    public Boolean addAmLocalAccount(Long uid, Long platform_id, String platform, String account, String mobile, String city) {
        AmZpLocalAccouts entity = new AmZpLocalAccouts();
        entity.setPlatform(platform);
        entity.setAccount(account);
        entity.setAdminId(uid);
        entity.setCity(city);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsRunning(0);
        entity.setIsSync(0);
        entity.setMobile(mobile);
        entity.setState("active");
        entity.setPlatformId(platform_id);
        entity.setType(0);
        entity.setUserId(null);
        entity.setBrowserId(null);
        entity.setExtBossId(null);

        int addResult = getBaseMapper().insert(entity);
        return addResult > 0;

    }

    public Boolean modifyRunningStatus(String id, int status) {
        // 创建更新条件
        LambdaUpdateWrapper<AmZpLocalAccouts> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AmZpLocalAccouts::getId, id).set(AmZpLocalAccouts::getIsRunning, status);
        // 执行更新操作
        return this.update(updateWrapper);

    }



    public Boolean modifyStatus(String id, String status) {
        // 创建更新条件
        LambdaUpdateWrapper<AmZpLocalAccouts> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AmZpLocalAccouts::getId, id).set(AmZpLocalAccouts::getState, status);
        // 执行更新操作
        return this.update(updateWrapper);
    }
}
