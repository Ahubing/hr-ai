package com.open.ai.eros.permission.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.permission.entity.Permission;
import com.open.ai.eros.db.mysql.permission.mapper.PermissionMapper;
import com.open.ai.eros.db.mysql.permission.service.impl.PermissionServiceImpl;
import com.open.ai.eros.permission.bean.req.PermissionCreateReq;
import com.open.ai.eros.permission.bean.req.PermissionUpdateReq;
import com.open.ai.eros.permission.bean.vo.PermissionVo;
import com.open.ai.eros.permission.convert.PermissionConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理---业务类
 */
@Slf4j
@Component
public class PermissionManager {

    @Resource
    private PermissionServiceImpl permissionService;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 创建新权限
     *
     * @param req
     * @return
     */
    public Integer createPermission(PermissionCreateReq req) {
        Permission permission = new Permission();
        permission.setPermission(req.getPermission());  // 权限英文名
        permission.setPermissionName(req.getPermissionName()); // 权限名称
        permission.setPermissionType(req.getPermissionType()); // 权限类型
        permission.setCreateTime(LocalDateTime.now());  // 权限创建时间
        permission.setModifyTime(LocalDateTime.now());  // 权限修改时间

        return permissionMapper.insert(permission);
    }


    /**
     * 批量删除权限
     * @param ids 要删除的权限ID列表
     * @return 是否全部删除成功
     */
    public boolean deletePermissions(List<Long> ids) {
        log.info("开始批量删除权限，ID列表：{}", ids);
        try {
            boolean deleted = permissionService.removeByIds(ids);
            log.info("批量删除权限{}。", deleted ? "成功" : "失败");
            return deleted;
        } catch (Exception e) {
            log.error("批量删除权限时发生错误：", e);
            return false;
        }
    }

    /**
     * 更新权限信息
     * @param req 权限更新请求
     * @return 是否更新成功
     */
    public boolean updatePermission(PermissionUpdateReq req) {
        log.info("开始更新权限，ID：{}", req.getId());
        try {
            Permission permission = permissionService.getById(req.getId());
            if (permission == null) {
                log.warn("未找到ID为{}的权限", req.getId());
                return false;
            }

            // 只更新非空字段
            if (req.getPermission() != null) {
                permission.setPermission(req.getPermission());
            }
            if (req.getPermissionName() != null) {
                permission.setPermissionName(req.getPermissionName());
            }
            if (req.getPermissionType() != null) {
                permission.setPermissionType(req.getPermissionType());
            }
            permission.setModifyTime(LocalDateTime.now());

            boolean updated = permissionService.updateById(permission);
            log.info("更新权限{}。", updated ? "成功" : "失败");
            return updated;
        } catch (Exception e) {
            log.error("更新权限时发生错误：", e);
            return false;
        }
    }

    /**
     * 分页查询权限列表，支持搜索
     *
     * @param current 当前页码
     * @param size 每页大小
     * @param permissionName 权限名称（可选）
     * @param permissionType 权限类型（可选）
     * @return 分页后的权限列表
     */
    public ResultVO<PageVO<PermissionVo>> getPermissionPage(Integer current, Integer size, String permissionName, Integer permissionType) {
        Page<Permission> page = new Page<>(current, size);
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(permissionName)) {
            queryWrapper.like(Permission::getPermissionName, permissionName);
        }
        if (permissionType != null) {
            queryWrapper.eq(Permission::getPermissionType, permissionType);
        }
        Page<Permission> permissionPage = permissionService.page(page, queryWrapper);
        List<PermissionVo> permissions = permissionPage.getRecords().stream().map(PermissionConvert.I::convertPermission).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(permissionPage.getTotal(),permissions));
    }

    /**
     * 查询全部权限列表，用户前端展示
     */
    public ResultVO<List<PermissionVo>> getAllPermission() {
        List<Permission> list = permissionService.list();
        List<PermissionVo> permissions = list.stream().map(PermissionConvert.I::convertPermission).collect(Collectors.toList());
        return ResultVO.success(permissions);
    }



    /**
     * 根据ID获取单个权限详情
     *
     * @param id 权限ID
     * @return 权限详情
     */
    public Permission getPermissionById(Long id) {
        return permissionService.getById(id);
    }

}
