package com.open.ai.eros.permission.manager;

import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.permission.entity.RolePermission;
import com.open.ai.eros.db.mysql.permission.entity.RolePermissionVo;
import com.open.ai.eros.db.mysql.permission.service.impl.RolePermissionServiceImpl;
import com.open.ai.eros.permission.bean.req.RolePermissionCreateReq;
import com.open.ai.eros.permission.bean.req.RolePermissionQueryReq;
import com.open.ai.eros.permission.bean.req.RolePermissionUpdateReq;
import com.open.ai.eros.permission.bean.vo.RolePermissionResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色管理---业务类
 */
@Slf4j
@Component
public class RolePermissionManager {

    @Resource
    private RolePermissionManager rolePermissionManager;

    @Resource
    private RolePermissionServiceImpl rolePermissionService;

    /**
     * 创建角色权限
     * @param req 创建角色权限请求
     * @return 创建后的角色权限对象
     */
    @Transactional
    public RolePermission createRolePermission(RolePermissionCreateReq req) {
        log.info("开始创建角色权限: {}", req);
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(req.getRole());
        rolePermission.setPermissionId(req.getPermissionId());
        rolePermission.setCreateTime(LocalDateTime.now());
        rolePermission.setModifyTime(LocalDateTime.now());
        rolePermissionService.save(rolePermission);
        log.info("角色权限创建成功: {}", rolePermission);
        return rolePermission;
    }

    /**
     * 根据ID获取角色权限
     * @return 角色权限对象
     */
    public List<RolePermissionResultVo> getRolePermissionByRole(String role) {
        List<RolePermissionResultVo> permissionResultVos = new ArrayList<>();

        List<RolePermissionVo> rolePermissionVos = rolePermissionService.getRolePermission(role);
        Map<String, List<RolePermissionVo>> groupedPermissions = rolePermissionVos.stream()
                .collect(Collectors.groupingBy(e->{
                    Integer permissionType = e.getPermissionType();
                    // 1 操作权限 2 页面模块权限
                    return permissionType.equals(1)?"button":"menu";
                }));

        for (Map.Entry<String, List<RolePermissionVo>> entry : groupedPermissions.entrySet()) {
            permissionResultVos.add(new RolePermissionResultVo(entry.getKey(),entry.getValue()));
        }
        return permissionResultVos;
    }

    /**
     * 更新角色权限
     * @param req 更新角色权限请求
     * @return 更新后的角色权限对象
     */
    @Transactional
    public RolePermission updateRolePermission(RolePermissionUpdateReq req) {
        log.info("开始更新角色权限: {}", req);
        RolePermission rolePermission = rolePermissionService.getById(req.getId());
        if (rolePermission == null) {
            throw new RuntimeException("角色权限不存在");
        }
        if (StringUtils.isNotBlank(req.getRole())) {
            rolePermission.setRole(req.getRole());
        }

        if (req.getPermissionId() != null) {
            rolePermission.setPermissionId(req.getPermissionId());
        }

        rolePermission.setModifyTime(LocalDateTime.now());
        rolePermissionService.updateById(rolePermission);
        log.info("角色权限更新成功: {}", rolePermission);
        return rolePermission;
    }

    /**
     * 删除角色权限
     * @param id 角色权限ID
     * @return 删除是否成功
     */
    public boolean deleteRolePermission(Integer id) {
        log.info("开始删除角色权限，ID: {}", id);
        boolean result = rolePermissionService.removeById(id);
        log.info("角色权限删除结果: {}", result);
        return result;
    }

    /**
     * 分页查询角色权限
     * @param req 查询角色权限请求
     * @return 分页结果
     */
    public ResultVO<PageVO<RolePermissionVo>> getRolePermissionPage(RolePermissionQueryReq req) {
        PageVO<RolePermissionVo> pageVO = rolePermissionService.getRolePermission(req.getRole(), req.getPageNum(), req.getPageSize());
        return ResultVO.success(pageVO);
    }

}
