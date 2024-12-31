package com.open.ai.eros.db.mysql.permission.mapper;

import com.open.ai.eros.db.mysql.permission.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-08
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

}
