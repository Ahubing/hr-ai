package com.open.ai.eros.db.mysql.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.db.mysql.user.entity.LoginLog;
import com.open.ai.eros.db.mysql.user.mapper.LoginLogMapper;
import com.open.ai.eros.db.mysql.user.service.ILoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 登录日志表，存储用户的登录日志信息 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-13
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements ILoginLogService {


}
