package com.open.ai.eros.db.mysql.permission.service.impl;

import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.db.mysql.permission.entity.RolePermission;
import com.open.ai.eros.db.mysql.permission.entity.RolePermissionVo;
import com.open.ai.eros.db.mysql.permission.mapper.RolePermissionMapper;
import com.open.ai.eros.db.mysql.permission.service.IRolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-08
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements IRolePermissionService {


    /**
     * 角色
     *
     * @param role
     * @return
     */
    public List<RolePermissionVo> getRolePermission(String role) {
        return this.getBaseMapper().getRolePermission(role);
    }


    /**
     * 分页查询权限
     *
     * @param role
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageVO<RolePermissionVo> getRolePermission(String role, Integer pageNum, Integer pageSize) {
        PageVO<RolePermissionVo> pageVO = new PageVO<>();
        int count = this.getBaseMapper().getRolePermissionCount(role);
        pageVO.setTotal(count);
        if (count > 0) {
            Integer pageIndex = (pageNum - 1) * pageSize;
            List<RolePermissionVo> rolePermissionVos = this.getBaseMapper().getRolePermissionByPage(role, pageIndex, pageSize);
            pageVO.setData(rolePermissionVos);
        }
        return pageVO;
    }


}
