package com.open.ai.eros.db.mysql.user.service.impl;

import com.open.ai.eros.db.mysql.user.entity.UserRoleApply;
import com.open.ai.eros.db.mysql.user.mapper.UserRoleApplyMapper;
import com.open.ai.eros.db.mysql.user.service.IUserRoleApplyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色申请表，存储用户的角色申请信息 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */
@Service
public class UserRoleApplyServiceImpl extends ServiceImpl<UserRoleApplyMapper, UserRoleApply> implements IUserRoleApplyService {

}
