package com.open.ai.eros.db.mysql.hr.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.hr.entity.AmModel;
import com.open.ai.eros.db.mysql.hr.mapper.AmModelMapper;
import com.open.ai.eros.db.mysql.hr.service.IAmModelService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 面具 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-09
 */
@Service
public class AmModelServiceImpl extends ServiceImpl<AmModelMapper, AmModel> implements IAmModelService {

    public AmModel getDefaultModel() {
        QueryWrapper<AmModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_default", 1);

        return baseMapper.selectOne(queryWrapper);
    }

}
