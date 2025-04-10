package com.open.ai.eros.permission.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.permission.entity.Permission;
import com.open.ai.eros.permission.bean.req.PermissionCreateReq;
import com.open.ai.eros.permission.bean.req.PermissionUpdateReq;
import com.open.ai.eros.permission.bean.vo.PermissionVo;
import com.open.ai.eros.permission.config.PermissionBaseController;
import com.open.ai.eros.permission.manager.PermissionManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 *
 * 权限管理---控制类
 *
 * @author Eros-AI
 * @since 2024-08-08 22：27
 */
@Slf4j
@Api(tags = "权限管理")
@RestController
public class PermissionController extends PermissionBaseController {

    @Autowired
    private PermissionManager permissionManager;


    /**
     * 创建新权限
     * @param req 权限创建请求参数
     * @return 返回创建结果信息
     */
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/permission/createPermission")
    @ApiOperation(value = "创建新权限", notes = "创建一个新的权限")
    public ResultVO createPermission(@Valid @RequestBody PermissionCreateReq req) {
        Integer permission = permissionManager.createPermission(req);
        return permission == 0 ? ResultVO.fail("创建失败") : ResultVO.success("创建成功");
    }
    /**
     * 更新权限信息
     * @param req 权限更新请求参数
     * @return 返回更新结果信息
     */
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/permission/updatePermission")
    @ApiOperation(value = "更新权限", notes = "根据权限ID更新权限信息")
    public ResultVO updatePermission(@Valid @RequestBody PermissionUpdateReq req) {
        boolean updated = permissionManager.updatePermission(req);
        return updated ? ResultVO.success("更新成功") : ResultVO.fail("更新失败");
    }

    /**
     * 批量删除权限
     * @param ids 要删除的权限ID列表
     * @return 返回删除结果信息
     */
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/permission/deletePermissions")
    @ApiOperation(value = "批量删除权限", notes = "根据权限ID列表批量删除权限")
    public ResultVO deletePermissions(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResultVO.fail("权限ID列表不能为空");
        }
        boolean deleted = permissionManager.deletePermissions(ids);
        return deleted ? ResultVO.success("批量删除成功") : ResultVO.fail("批量删除失败");
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
    @VerifyUserToken
    @GetMapping("/page")
    @ApiOperation(value = "分页查询权限", notes = "分页查询权限列表，支持按名称和类型搜索")
    public ResultVO<PageVO<PermissionVo>> getPermissionPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) Integer permissionType) {
        return permissionManager.getPermissionPage(current, size, permissionName, permissionType);
    }


    /**
     * 查询全部权限列表，用户前端展示
     */
    @VerifyUserToken
    @GetMapping("/getAll")
    @ApiOperation(value = "查询权限列表", notes = "查询全部权限列表，用户前端展示")
    public ResultVO<List<PermissionVo>> getAllPermission() {
        return permissionManager.getAllPermission();
    }
    /**
     * 根据ID获取单个权限详情
     *
     * @param id 权限ID
     * @return 权限详情
     */
    @VerifyUserToken
    @GetMapping("/{id}")
    @ApiOperation(value = "获取单个权限详情", notes = "根据权限ID获取权限详情")
    public ResultVO<Permission> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionManager.getPermissionById(id);
        return permission != null ? ResultVO.success(permission) : ResultVO.fail("未找到指定权限");
    }

}
