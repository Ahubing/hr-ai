package com.open.ai.eros.db.mysql.hr.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import com.open.ai.eros.db.mysql.hr.mapper.AmZpPlatformsMapper;
import com.open.ai.eros.db.mysql.hr.service.IAmZpPlatformsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.User;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 招聘平台
 * 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Service
public class AmZpPlatformsServiceImpl extends ServiceImpl<AmZpPlatformsMapper, AmZpPlatforms> implements IAmZpPlatformsService {

    public int addPlatForm(String name) {
        AmZpPlatforms amZpPlatform = new AmZpPlatforms();
        amZpPlatform.setName(name);
        return getBaseMapper().insert(amZpPlatform);
    }

    public Boolean modifyPlatformName(Long id, String name) {
        // 创建更新条件
        LambdaUpdateWrapper<AmZpPlatforms> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AmZpPlatforms::getId, id).set(AmZpPlatforms::getName, name);
        // 执行更新操作
        return this.update(updateWrapper);
    }

    public Boolean deletePlatformName(Long id) {
        int delResult = getBaseMapper().deletePlatFormById(id);
        return delResult > 0;
    }
}
