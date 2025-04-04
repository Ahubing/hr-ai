package com.open.hr.ai.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.constants.AmAdminRoleEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import com.open.ai.eros.db.mysql.hr.entity.AmModel;
import com.open.ai.eros.db.mysql.hr.entity.AmNewMask;
import com.open.ai.eros.db.mysql.hr.service.impl.AmAdminServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmModelServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmNewMaskServiceImpl;
import com.open.hr.ai.bean.req.AmModelAddReq;
import com.open.hr.ai.bean.req.AmModelUpdateReq;
import com.open.hr.ai.bean.vo.AmModelVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * AI模型管理器
 * </p>
 */
@Slf4j
@Component
public class AmModelManager {

    @Autowired
    private AmModelServiceImpl amModelService;

    @Autowired
    private AmNewMaskServiceImpl amNewMaskService;

    @Resource
    private AmAdminServiceImpl amAdminService;


    /**
     * 获取所有模型
     */
    public ResultVO<Page<AmModel>> getAllModels(Page<AmModel> page, Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || admin.getRole().equals(AmAdminRoleEnum.VIP.getType())) {
            return ResultVO.fail("没有权限进行操作");
        }
        LambdaQueryWrapper<AmModel> queryWrapper = new LambdaQueryWrapper<>();

        // 执行分页查询
        Page<AmModel> amModelPage = amModelService.page(page, queryWrapper);

        // 返回分页结果
        return ResultVO.success(amModelPage);

    }
    /*public ResultVO<List<AmModel>> getAllModels(Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || admin.getRole().equals(AmAdminRoleEnum.VIP.getType())) {
            return ResultVO.fail("没有权限进行操作");
        }
        // 获取状态为启用的模型
        LambdaQueryWrapper<AmModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmModel::getStatus, 1);
        List<AmModel> models = amModelService.list(queryWrapper);
        return ResultVO.success(models);
    }*/

    /**
     * 根据ID获取模型
     */
    public ResultVO<AmModel> getModelById(Long id, Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || admin.getRole().equals(AmAdminRoleEnum.VIP.getType())) {
            return ResultVO.fail("没有权限进行操作");
        }
        AmModel model = amModelService.getById(id);
        if (model == null) {
            return ResultVO.fail("模型不存在");
        }
        return ResultVO.success(model);
    }

    /**
     * 添加模型
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<Boolean> addModel(AmModelAddReq req, Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || admin.getRole().equals(AmAdminRoleEnum.VIP.getType())) {
            return ResultVO.fail("没有权限进行操作");
        }
        // 验证模型名称是否重复
        LambdaQueryWrapper<AmModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmModel::getName, req.getName())
                .or()
                .eq(AmModel::getValue, req.getValue());
        int count = amModelService.count(queryWrapper);
        if (count > 0) {
            return ResultVO.fail("模型名称或值已存在");
        }
        // 如果设置为默认模型，需要取消其他默认模型
        if (req.getIsDefault() != null && req.getIsDefault() == 1) {
            resetDefaultModel();
        }
        AmModel model = new AmModel();
        BeanUtils.copyProperties(req, model);
        model.setStatus(1); // 默认启用
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        model.setCreateUserId(adminId);
        if (model.getIsDefault() == null) {
            model.setIsDefault(0);
        }

        boolean result = amModelService.save(model);
        return result ? ResultVO.success(true) : ResultVO.fail("添加失败");
    }

    /**
     * 更新模型
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<Boolean> updateModel(AmModelUpdateReq req, Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || admin.getRole().equals(AmAdminRoleEnum.VIP.getType())) {
            return ResultVO.fail("没有权限进行操作");
        }

        AmModel existModel = amModelService.getById(req.getId());
        if (existModel == null) {
            return ResultVO.fail("模型不存在");
        }

        // 验证模型名称和值是否与其他模型重复
        if (req.getName() != null || req.getValue() != null) {
            LambdaQueryWrapper<AmModel> queryWrapper = new LambdaQueryWrapper<>();
            if (req.getName() != null) {
                queryWrapper.eq(AmModel::getName, req.getName());
            }
            if (req.getValue() != null) {
                queryWrapper.or().eq(AmModel::getValue, req.getValue());
            }
            queryWrapper.ne(AmModel::getId, req.getId());
            int count = amModelService.count(queryWrapper);
            if (count > 0) {
                return ResultVO.fail("模型名称或值已存在");
            }
        }

        AmModel model = new AmModel();
        BeanUtils.copyProperties(req, model);
        model.setUpdateTime(LocalDateTime.now());

        boolean result = amModelService.updateById(model);
        return result ? ResultVO.success(true) : ResultVO.fail("更新失败");
    }

    /**
     * 删除模型
     */
    public ResultVO<Boolean> deleteModel(Long id, Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || admin.getRole().equals(AmAdminRoleEnum.VIP.getType())) {
            return ResultVO.fail("没有权限进行操作");
        }
        AmModel model = amModelService.getById(id);
        if (model == null) {
            return ResultVO.fail("模型不存在");
        }
        // 不允许删除默认模型
        if (model.getIsDefault() != null && model.getIsDefault() == 1) {
            return ResultVO.fail("默认模型不能删除");
        }
        // 检查模型是否被面具引用
        LambdaQueryWrapper<AmNewMask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmNewMask::getModelId, id);
        int count = amNewMaskService.count(queryWrapper);
        if (count > 0) {
            return ResultVO.fail("该模型已被面具使用，不能删除");
        }
        boolean result = amModelService.removeById(id);
        return result ? ResultVO.success(true) : ResultVO.fail("删除失败");
    }

    /**
     * 启用/禁用模型
     */
    public ResultVO<Boolean> toggleModelStatus(Long id, Integer status, Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || admin.getRole().equals(AmAdminRoleEnum.VIP.getType())) {
            return ResultVO.fail("没有权限进行操作");
        }
        AmModel model = amModelService.getById(id);
        if (model == null) {
            return ResultVO.fail("模型不存在");
        }

        // 不允许禁用默认模型
        if (status == 0 && model.getIsDefault() != null && model.getIsDefault() == 1) {
            return ResultVO.fail("默认模型不能禁用");
        }

        LambdaUpdateWrapper<AmModel> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AmModel::getId, id)
                .set(AmModel::getStatus, status)
                .set(AmModel::getUpdateTime, LocalDateTime.now());

        boolean result = amModelService.update(updateWrapper);
        return result ? ResultVO.success(true) : ResultVO.fail("操作失败");
    }

    /**
     * 设置默认模型
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO<Boolean> setDefaultModel(Long id, Long adminId) {
        AmAdmin admin = amAdminService.getById(adminId);
        if (admin.getRole().equals(AmAdminRoleEnum.COMMON.getType()) || admin.getRole().equals(AmAdminRoleEnum.VIP.getType())) {
            return ResultVO.fail("没有权限进行操作");
        }

        AmModel model = amModelService.getById(id);
        if (model == null) {
            return ResultVO.fail("模型不存在");
        }

        // 检查模型是否已被禁用
        if (model.getStatus() != null && model.getStatus() == 0) {
            return ResultVO.fail("禁用的模型不能设为默认模型");
        }

        // 重置所有模型的默认标志
        resetDefaultModel();

        // 设置新的默认模型
        LambdaUpdateWrapper<AmModel> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AmModel::getId, id)
                .set(AmModel::getIsDefault, 1)
                .set(AmModel::getUpdateTime, LocalDateTime.now());

        boolean result = amModelService.update(updateWrapper);
        return result ? ResultVO.success(true) : ResultVO.fail("设置默认模型失败");
    }

    /**
     * 重置所有默认模型
     */
    private void resetDefaultModel() {
        LambdaUpdateWrapper<AmModel> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AmModel::getIsDefault, 0);
        amModelService.update(updateWrapper);
    }

    /**
     * 获取所有可用模型（用于下拉列表）
     */
    public ResultVO<List<AmModel>> getAvailableModelsForSelect() {
        // 只获取状态为启用的模型
        LambdaQueryWrapper<AmModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmModel::getStatus, 1)
                .orderByDesc(AmModel::getIsDefault) // 默认模型排在前面
                .orderByDesc(AmModel::getCreateTime); // 然后按创建时间倒序

        List<AmModel> models = amModelService.list(queryWrapper);
        return ResultVO.success(models);
    }

    public List<AmModel> getAvailableModels() {
        LambdaQueryWrapper<AmModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmModel::getStatus, 1);  // 只查询可用状态的模型

        List<AmModel> models = amModelService.list(queryWrapper);

        //转换为 VO 只返回 id 和 name
    /*return models.stream()
            .map(model -> new AmModelVO(model.getId(), model.getName()))
            .collect(Collectors.toList());*/
        return models;
    }
}