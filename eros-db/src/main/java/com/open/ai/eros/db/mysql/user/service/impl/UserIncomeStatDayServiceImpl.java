package com.open.ai.eros.db.mysql.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.user.entity.UserIncomeStatDay;
import com.open.ai.eros.db.mysql.user.mapper.UserIncomeStatDayMapper;
import com.open.ai.eros.db.mysql.user.service.IUserIncomeStatDayService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户收益日统计表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Service
public class UserIncomeStatDayServiceImpl extends ServiceImpl<UserIncomeStatDayMapper, UserIncomeStatDay> implements IUserIncomeStatDayService {


    /**
     * 获取收益
     *
     * @return
     */
    public UserIncomeStatDay getLastStatDay() {
        return this.baseMapper.getLastStatDay();
    }

}
