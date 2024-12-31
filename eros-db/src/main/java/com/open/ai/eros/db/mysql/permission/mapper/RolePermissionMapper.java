package com.open.ai.eros.db.mysql.permission.mapper;

import com.open.ai.eros.db.mysql.permission.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.permission.entity.RolePermissionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 角色权限表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-08
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {


    @Select(" select b.permission , b.permission_name , b.permission_type from  role_permission a , permission b where a.role = #{role} and a.permission_id = b.id ")
    List<RolePermissionVo> getRolePermission(@Param("role") String role);

    @Select("<script> select a.id, a.role, a.permission_id as permissionId, b.permission, b.permission_name, b.permission_type from role_permission a, permission b where a.permission_id = b.id <if test=\"role != null and role != ''\"> and a.role = #{role} </if> limit #{pageIndex} ,#{pageSize}</script>")
    List<RolePermissionVo> getRolePermissionByPage(@Param("role") String role, @Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize);

    @Select("<script> select count(1) from role_permission a , permission b where a.permission_id = b.id <if test='role!=null'>and a.role = #{role}</if></script>")
    int getRolePermissionCount(@Param("role") String role);


}
