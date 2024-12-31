package com.open.ai.eros.permission.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.MenuEnum;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.MenuVo;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.permission.entity.RolePermission;
import com.open.ai.eros.permission.bean.req.RolePermissionCreateReq;
import com.open.ai.eros.permission.bean.req.RolePermissionQueryReq;
import com.open.ai.eros.permission.bean.req.RolePermissionUpdateReq;
import com.open.ai.eros.db.mysql.permission.entity.RolePermissionVo;
import com.open.ai.eros.permission.bean.vo.RolePermissionResultVo;
import com.open.ai.eros.permission.config.PermissionBaseController;
import com.open.ai.eros.permission.manager.RolePermissionManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 角色管理---控制类
 */
@Slf4j
@Api(tags = "角色管理")
@RestController
public class RolePermissionController extends PermissionBaseController {

    @Resource
    private RolePermissionManager rolePermissionManager;

    /**
     * 创建角色权限
     * @param req 请求参数类
     * @return
     */
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/role/permission/create")
    @ApiOperation(value = "创建角色权限", notes = "创建新的角色权限")
    public ResultVO<RolePermission> createRolePermission(@Valid @RequestBody RolePermissionCreateReq req) {
        RolePermission created = rolePermissionManager.createRolePermission(req);
        return ResultVO.success(created);
    }

    /**
     * 获取角色权限
     * @return 返回查询到的角色信息
     */
    @VerifyUserToken
    @GetMapping("/role/permission/list")
    @ApiOperation(value = "获取角色权限", notes = "根据ID获取角色权限")
    public ResultVO<List<RolePermissionResultVo>> getRolePermission() {
        List<RolePermissionResultVo> rolePermissionByRole = rolePermissionManager.getRolePermissionByRole(getRole());
        return ResultVO.success(rolePermissionByRole);
    }

    /**
     * 更新角色权限
     * @param req 请求参数类
     * @return
     */
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/role/permission/update")
    @ApiOperation(value = "更新角色权限", notes = "更新指定ID的角色权限")
    public ResultVO<RolePermission> updateRolePermission(@Valid @RequestBody RolePermissionUpdateReq req) {
        RolePermission updated = rolePermissionManager.updateRolePermission(req);
        return ResultVO.success(updated);
    }


    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/role/permission/delete")
    @ApiOperation(value = "删除角色权限", notes = "删除指定ID的角色权限")
    public ResultVO<Boolean> deleteRolePermission(@Param("id") Integer id) {
        boolean result = rolePermissionManager.deleteRolePermission(id);
        return ResultVO.success(result);
    }


    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @PostMapping("/role/permission/page")
    @ApiOperation(value = "分页查询角色权限", notes = "分页查询角色权限列表，支持按角色和权限ID搜索")
    public ResultVO<PageVO<RolePermissionVo>> getRolePermissionPage(@Valid @RequestBody RolePermissionQueryReq req) {
        return rolePermissionManager.getRolePermissionPage(req);
    }

    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @GetMapping("/menu/list")
    public ResultVO<List<MenuVo>> getMenu(){
        return ResultVO.success(MenuEnum.getRoleMenu(getRole()));
    }

}
