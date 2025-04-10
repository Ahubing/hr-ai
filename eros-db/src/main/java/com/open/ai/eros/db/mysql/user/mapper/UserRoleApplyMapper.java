package com.open.ai.eros.db.mysql.user.mapper;

import com.open.ai.eros.db.mysql.user.entity.UserRoleApply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色申请表，存储用户的角色申请信息 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */
@Mapper
public interface UserRoleApplyMapper extends BaseMapper<UserRoleApply> {

}
