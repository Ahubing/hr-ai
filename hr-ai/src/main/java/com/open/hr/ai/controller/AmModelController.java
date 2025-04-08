package com.open.hr.ai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.vo.AmGreetConditionVo;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.*;
import com.open.hr.ai.config.HrAIBaseController;
//import com.open.hr.ai.constant.ClientTaskTypeEnums;
import com.open.hr.ai.manager.AmClientTaskManager;
import com.open.hr.ai.manager.AmModelManager;
import com.open.hr.ai.manager.ChatBotManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Date 2025/1/4 13:04
 */


@Api(tags = "Model管理")
@Slf4j
@RestController
public class AmModelController extends HrAIBaseController {

    @Resource
    private AmModelManager amModelManager;

    @ApiOperation(value = "获取所有AI模型(分页查询)", notes = "获取系统中所有AI模型，包括禁用的模型")
    @VerifyUserToken
    @GetMapping("/amModel/list/all")
    public ResultVO<Page<AmModel>> getAllModels(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<AmModel> page = new Page<>(pageNum, pageSize);
        return amModelManager.getAllModels(page, getUserId());
    }

    @ApiOperation(value = "根据id查询AI模型", notes = "根据ID获取AI模型的详细配置信息")
    @VerifyUserToken
    @GetMapping("/amModel/get/{id}")
    public ResultVO<AmModel> getModelById(@PathVariable Long id) {
        return amModelManager.getModelById(id, getUserId());
    }

    @ApiOperation(value = "添加AI模型", notes = "添加新的AI模型配置")
    @VerifyUserToken
    @PostMapping("/amModel/add")
    public ResultVO<Boolean> addModel(@RequestBody @Valid AmModelAddReq req) {
        return amModelManager.addModel(req, getUserId());
    }

    @ApiOperation(value = "更新AI模型", notes = "更新现有AI模型的配置信息")
    @VerifyUserToken
    @PostMapping("/amModel/update")
    public ResultVO<Boolean> updateModel(@RequestBody @Valid AmModelUpdateReq req) {
        return amModelManager.updateModel(req, getUserId());
    }

    @ApiOperation(value = "删除AI模型", notes = "删除指定的AI模型")
    @VerifyUserToken
    @PostMapping("/amModel/delete/{id}")
    public ResultVO<Boolean> deleteModel(@PathVariable Long id) {
        return amModelManager.deleteModel(id, getUserId());
    }

    @ApiOperation(value = "启用/禁用AI模型", notes = "修改AI模型的启用状态")
    @VerifyUserToken
    @PostMapping("/amModel/status/{id}/{status}")
    public ResultVO<Boolean> toggleModelStatus(@PathVariable Long id, @PathVariable Integer status) {
        return amModelManager.toggleModelStatus(id, status, getUserId());
    }

    @ApiOperation(value = "设置默认AI模型", notes = "将指定模型设置为系统默认模型")
    @VerifyUserToken
    @PostMapping("/amModel/set-default/{id}")
    public ResultVO<Boolean> setDefaultModel(@PathVariable Long id) {
        return amModelManager.setDefaultModel(id, getUserId());
    }

    @ApiOperation(value = "获取可用模型列表", notes = "获取所有可用的模型，用于下拉列表选择")
    @VerifyUserToken
    @GetMapping("/amModel/select/list")
    public ResultVO<List<AmModel>> getModelList() {
        List<AmModel> models = amModelManager.getAvailableModels(getUserId());
        return ResultVO.success(models);
    }

}

