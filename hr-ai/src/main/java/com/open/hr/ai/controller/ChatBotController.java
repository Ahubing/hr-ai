package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.vo.AmGreetConditionVo;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.*;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.ai.eros.common.constants.ClientTaskTypeEnums;
import com.open.hr.ai.manager.AmClientTaskManager;
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

/**
 * @Date 2025/1/4 13:04
 */


@Api(tags = "ChatBot管理类")
@Slf4j
@RestController
public class ChatBotController extends HrAIBaseController {


    @Resource
    private ChatBotManager chatBotManager;


    @Autowired
    private AmClientTaskManager amClientTaskManager;

    @VerifyUserToken
    @GetMapping("chatbot/get_local_accounts")
    @ApiOperation(value = "获取本地登录账号列表", notes = "获取本地登录账号列表", httpMethod = "GET", response = ResultVO.class)
    public ResultVO<AmZmLocalAccountsListVo> getLocalAccounts() {
        Long adminId = getUserId();
        return chatBotManager.getLocalAccounts(adminId);
    }


    @VerifyUserToken
    @GetMapping("chatbot/platforms")
    @ApiOperation(value = "获取招聘平台列表(与多账号登录共用)", notes = "获取招聘平台列表(与多账号登录共用)", httpMethod = "GET", response = ResultVO.class)
    public ResultVO<List<AmZpPlatforms>> getPlatforms() {
        return chatBotManager.getPlatforms();
    }


    @VerifyUserToken
    @PostMapping("chatbot/add_account")
    @ApiOperation(value = "添加本地招聘账号", notes = "添加本地招聘账号", httpMethod = "POST", response = ResultVO.class)
    public ResultVO AddAccount(@RequestBody @Valid AddOrUpdateAccountReq addOrUpdateAccountReq) {
        if (Objects.isNull(addOrUpdateAccountReq)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.AddAccount(addOrUpdateAccountReq, getUserId());
    }

    /**
     * 修改本地招聘账号
     *
     * @return
     */
    @VerifyUserToken
    @PostMapping("/chatbot/modify_account")
    @ApiOperation(value = "修改本地招聘账号", notes = "修改本地招聘账号", httpMethod = "POST", response = ResultVO.class)
    public ResultVO modifyAccount(@RequestBody @Valid AddOrUpdateAccountReq modifyAccountReq) {
        if (Objects.isNull(modifyAccountReq)) {
            return ResultVO.fail("参数不能为空");
        }
        if (Objects.isNull(modifyAccountReq.getId())) {
            return ResultVO.fail("账号id不能为空");
        }
        return chatBotManager.updateAccount(modifyAccountReq, getUserId());
    }


    @ApiOperation(value = "获取boss任务数", notes = "获取boss任务数", httpMethod = "GET", response = ResultVO.class)
    @VerifyUserToken
    @GetMapping("/zp/get/taskData")
    public ResultVO getTaskData(@RequestParam(value = "id", required = true) String bossId) {
        return amClientTaskManager.getTaskList(bossId);
    }


    @ApiOperation(value = "获取boss正在执行的任务数据", notes = "获取boss任务数据", httpMethod = "GET", response = ResultVO.class)
    @VerifyUserToken
    @GetMapping("/zp/get/taskList")
    public ResultVO getTaskList(@RequestParam(value = "id", required = true) String bossId, @RequestParam(value = "limit", required = false) Integer limit) {
        return amClientTaskManager.getExecuteTask(bossId,limit);
    }

    @ApiOperation(value = "获取boss任务已完成的任务数据", notes = "获取boss任务数据", httpMethod = "GET", response = ResultVO.class)
    @VerifyUserToken
    @GetMapping("/zp/get/taskPage")
    public ResultVO getTaskListPage(@RequestParam(value = "id", required = true) String bossId,@RequestParam(value = "page", required = true)Integer page,@RequestParam(value = "pageSize", required = true)Integer pageSize) {
        return amClientTaskManager.getDoneTask(bossId,page,pageSize);
    }

    @ApiOperation(value = "删除boss任务", notes = "删除boss任务", httpMethod = "GET", response = ResultVO.class)
    @VerifyUserToken
    @GetMapping("/zp/delete/clientTask")
    public ResultVO getTaskData(@RequestParam(value = "id", required = true) String bossId, @RequestParam(value = "taskType", required = false) String taskType) {
        return amClientTaskManager.deleteAmClientTask(bossId, taskType);
    }


    @ApiOperation(value = "获取boss任务类型", notes = "获取boss任务类型", httpMethod = "GET", response = ResultVO.class)
    @VerifyUserToken
    @GetMapping("/zp/get/clientTask/type")
    public ResultVO getClientTaskType() {
        HashMap<String, String> map = new HashMap<>();
        map.put("rechat", "复聊任务");
        map.put(ClientTaskTypeEnums.GREET.getType(), ClientTaskTypeEnums.GREET.getDesc());
        map.put(ClientTaskTypeEnums.SEND_MESSAGE.getType(), ClientTaskTypeEnums.SEND_MESSAGE.getDesc());
        map.put(ClientTaskTypeEnums.REQUEST_INFO.getType(), ClientTaskTypeEnums.REQUEST_INFO.getDesc());
        map.put(ClientTaskTypeEnums.GET_ALL_JOB.getType(), ClientTaskTypeEnums.GET_ALL_JOB.getDesc());
        return ResultVO.success(map);
    }


    @VerifyUserToken
    @GetMapping("/chatbot/delete_account")
    @ApiOperation(value = "删除本地招聘账号", notes = "删除本地招聘账号", httpMethod = "GET", response = ResultVO.class)
    public ResultVO deleteAccount(@RequestParam(value = "id", required = true) String id) {
        return chatBotManager.deleteAccount(id);
    }


    @VerifyUserToken
    @GetMapping("/chatbot/get_greet_by_account_id")
    @ApiOperation(value = "根据账号id获取打招呼设置", notes = "根据账号id获取打招呼设置", httpMethod = "GET", response = ResultVO.class)
    public ResultVO<AmZpLocalAccoutsVo> getGreetByAccountId(@RequestParam(value = "id", required = true) String id) {
        return chatBotManager.getGreetByAccountId(id);
    }

    @VerifyUserToken
    @PostMapping("/chatbot/get_greet_config")
    @ApiOperation(value = "获取打招呼配置信息", notes = "获取打招呼配置信息", httpMethod = "POST", response = ResultVO.class)
    public ResultVO<AmChatBotGreetConfigDataVo> getGreetConfig(@RequestBody @Valid SearchGreetConfig searchGreetConfig) {
        if (Objects.isNull(searchGreetConfig)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.getGreetConfig(searchGreetConfig.getAccount_id(), getUserId());
    }


    @VerifyUserToken
    @GetMapping("/chatbot/get_options_config")
    @ApiOperation(value = "获取方案配置", notes = "获取方案配置", httpMethod = "GET", response = ResultVO.class)
    public ResultVO<AmChatbotOptionsConfig> getOptionsConfig() {
        Long userId = getUserId();
        return chatBotManager.getOptionsConfig(userId);
    }


    @VerifyUserToken
    @PostMapping("/chatbot/modify_options_config")
    @ApiOperation(value = "修改方案配置", notes = "修改方案配置", httpMethod = "POST", response = ResultVO.class)
    public ResultVO<AmChatbotOptionsConfig> modifyOptionsConfig(@RequestBody @Valid UpdateOptionsConfigReq updateOptionsConfigReq) {
        if (Objects.isNull(updateOptionsConfigReq)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyOptionsConfig(updateOptionsConfigReq, getUserId());
    }


    @VerifyUserToken
    @PostMapping("/chatbot/sync_positions")
    @ApiOperation(value = "同步职位", notes = "同步职位", httpMethod = "POST", response = ResultVO.class)
    public ResultVO syncPositions(@RequestBody @Valid SyncPositionsReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.syncPositions(req);
    }


    @VerifyUserToken
    @PostMapping("/chatbot/modify_all_status")
    @ApiOperation(value = "修改全部的状态", notes = "修改全部的状态", httpMethod = "POST", response = ResultVO.class)
    public ResultVO modifyAllStatus(@RequestBody @Valid UpdateGreetConfigStatusReq req) {
        if (Objects.isNull(req) || Objects.isNull(req.getIsGreetOn()) || Objects.isNull(req.getIsRechatOn()) || Objects.isNull(req.getIsAiOn())) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyAllStatus(req);
    }

    @VerifyUserToken
    @PostMapping("/chatbot/modify_greet_status")
    @ApiOperation(value = "修改打招呼的状态", notes = "修改打招呼的状态", httpMethod = "POST", response = ResultVO.class)
    public ResultVO modifyGreetStatus(@RequestBody @Valid UpdateGreetConfigStatusReq req) {
        if (Objects.isNull(req) || Objects.isNull(req.getIsGreetOn())) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyGreetStatus(req);
    }


    @ApiOperation("修改复聊任务的状态")
    @VerifyUserToken
    @PostMapping("/chatbot/modify_rechat_status")
    public ResultVO modifyReChatStatus(@RequestBody @Valid UpdateGreetConfigStatusReq req) {

        if (Objects.isNull(req) || Objects.isNull(req.getIsRechatOn())) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyReChatStatus(req);
    }


    @ApiOperation("修改总开关任务的状态")
    @VerifyUserToken
    @PostMapping("/chatbot/modify_All_on_status")
    public ResultVO modifyAllOnStatus(@RequestBody @Valid UpdateGreetConfigStatusReq req) {

        if (Objects.isNull(req) || Objects.isNull(req.getIsRechatOn())) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyAllOnStatus(req);
    }


    @ApiOperation("修改AI跟进任务的状态")
    @VerifyUserToken
    @PostMapping("/chatbot/modify_ai_status")
    public ResultVO modifyAiStatus(@RequestBody @Valid UpdateGreetConfigStatusReq req) {

        if (Objects.isNull(req) || Objects.isNull(req.getIsAiOn())) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyAIOnStatus(req);
    }


    @ApiOperation("设置打招呼筛选条件")
    @VerifyUserToken
    @PostMapping("/chatbot/set_greet_condition")
    public ResultVO<AmGreetConditionVo> setGreetCondition(@RequestBody @Valid AddOrUpdateChatbotGreetConditionNew req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.setGreetCondition(req);
    }


    @ApiOperation("添加/编辑任务")
    @VerifyUserToken
    @PostMapping("/chatbot/set_greet_task")
    public ResultVO<AmChatbotGreetTask> setGreetTask(@RequestBody @Valid AddOrUpdateAmChatbotGreetTask req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.setGreetTask(req);
    }


    @ApiOperation("获取打招呼任务列表")
    @VerifyUserToken
    @PostMapping("/chatbot/get_greet_tasks")
    public ResultVO<List<AmChatbotGreetTaskVo>> getGreetTasks(@RequestBody @Valid SearchAmChatbotGreetTask req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.getGreetTasks(req);
    }

    @ApiOperation("删除打招呼任务")
    @VerifyUserToken
    @GetMapping("/chatbot/delete_task")
    public ResultVO deleteGreetTask(@RequestParam(value = "id", required = true) Integer id) {
        return chatBotManager.deleteGreetTask(id);
    }


    @ApiOperation("根据职位id获取筛选条件")
    @VerifyUserToken
    @GetMapping("/chatbot/get_condition_by_position_id")
    public ResultVO<AmGreetConditionVo> getConditionByPositionId(@RequestParam(value = "id", required = true) Integer id) {
        return chatBotManager.getConditionByPositionId(id);
    }


    @ApiOperation("获取职位的方案设置")
    @VerifyUserToken
    @PostMapping("/chatbot/get_position_options")
    public ResultVO<List<AmChatbotPositionOptionVo>> getPositionOptions(@RequestBody @Valid SearchPositionOptions req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.getPositionOptions(req);
    }


    @ApiOperation("设置职位方案")
    @VerifyUserToken
    @PostMapping("/chatbot/set_position_option")
    public ResultVO<AmChatbotPositionOption> setPositionOption(@RequestBody @Valid AddPositionOptions req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        if (Objects.isNull(req.getAmMaskId()) && Objects.isNull(req.getRechatOptionId())) {
            return ResultVO.fail("打招呼方案跟复聊方案不能同时为空");
        }
        return chatBotManager.setPositionOption(req);
    }


    @ApiOperation("获取打招呼筛选条件")
    @VerifyUserToken
    @PostMapping("/chatbot/get_greet_condition")
    public ResultVO<AmGreetConditionStaticVo> getGreetCondition() {
        return chatBotManager.getAmGreetConditionStaticVo();
    }

}
