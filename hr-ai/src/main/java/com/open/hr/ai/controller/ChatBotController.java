package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.*;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.ChatBotManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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

    @VerifyUserToken
    @GetMapping("chatbot/get_local_accounts")
    @ApiOperation(value = "获取本地登录账号列表",notes = "获取本地登录账号列表", httpMethod = "GET", response = ResultVO.class)
    public ResultVO<AmZmLocalAccountsListVo> getLocalAccounts() {
        Long adminId = getUserId();
        return chatBotManager.getLocalAccounts(adminId);
    }


    @VerifyUserToken
    @GetMapping("chatbot/platforms")
    @ApiOperation(value = "获取招聘平台列表(与多账号登录共用)",notes = "获取招聘平台列表(与多账号登录共用)", httpMethod = "GET", response = ResultVO.class)
    public ResultVO<List<AmZpPlatforms>> getPlatforms() {
        return chatBotManager.getPlatforms();
    }



    @VerifyUserToken
    @PostMapping("chatbot/add_account")
    @ApiOperation(value = "添加本地招聘账号",notes = "添加本地招聘账号", httpMethod = "POST", response = ResultVO.class)
    public ResultVO AddAccount(@RequestBody @Valid AddAccountReq addAccountReq) {
        if (Objects.isNull(addAccountReq)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.AddAccount(addAccountReq,getUserId());
    }


    @VerifyUserToken
    @GetMapping("/chatbot/delete_account")
    @ApiOperation(value = "删除本地招聘账号",notes = "删除本地招聘账号", httpMethod = "GET", response = ResultVO.class)
    public ResultVO deleteAccount(@RequestParam(value = "id",required = true) String id) {
        return chatBotManager.deleteAccount(id);
    }



    @VerifyUserToken
    @GetMapping("/chatbot/get_greet_by_account_id")
    @ApiOperation(value = "根据账号id获取打招呼设置",notes = "根据账号id获取打招呼设置", httpMethod = "GET", response = ResultVO.class)
    public ResultVO<AmZpLocalAccoutsVo> getGreetByAccountId(@RequestParam(value = "id",required = true) String id) {
        return chatBotManager.getGreetByAccountId(id);
    }

    @VerifyUserToken
    @PostMapping("/chatbot/get_greet_config")
    @ApiOperation(value = "获取打招呼配置信息",notes = "获取打招呼配置信息", httpMethod = "POST", response = ResultVO.class)
    public ResultVO<AmChatBotGreetConfigDataVo> getGreetConfig(@RequestBody @Valid SearchGreetConfig searchGreetConfig) {
        if (Objects.isNull(searchGreetConfig)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.getGreetConfig(searchGreetConfig.getAccount_id(),getUserId());
    }


    @VerifyUserToken
    @GetMapping("/chatbot/get_options_config")
    @ApiOperation(value = "获取方案配置",notes = "获取方案配置", httpMethod = "GET", response = ResultVO.class)
    public ResultVO<AmChatbotOptionsConfig> getOptionsConfig() {
        Long userId = getUserId();
        return chatBotManager.getOptionsConfig(userId);
    }




    @VerifyUserToken
    @PostMapping("/chatbot/modify_options_config")
    @ApiOperation(value = "修改方案配置",notes = "修改方案配置", httpMethod = "POST", response = ResultVO.class)
    public ResultVO<AmChatbotOptionsConfig> modifyOptionsConfig(@RequestBody @Valid UpdateOptionsConfigReq updateOptionsConfigReq) {
        if (Objects.isNull(updateOptionsConfigReq)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyOptionsConfig(updateOptionsConfigReq,getUserId());
    }


    @VerifyUserToken
    @PostMapping("/chatbot/sync_positions")
    @ApiOperation(value = "同步职位",notes = "同步职位", httpMethod = "POST", response = ResultVO.class)
    public ResultVO syncPositions(@RequestBody @Valid SyncPositionsReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.syncPositions(req);
    }


    @VerifyUserToken
    @PostMapping("/chatbot/modify_all_status")
    @ApiOperation(value = "修改全部的状态",notes = "修改全部的状态", httpMethod = "POST", response = ResultVO.class)
    public ResultVO modifyAllStatus(@RequestBody @Valid UpdateGreetConfigStatusReq req) {
        if (Objects.isNull(req) || Objects.isNull(req.getIsGreetOn()) || Objects.isNull(req.getIsRechatOn()) || Objects.isNull(req.getIsAiOn())) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyAllStatus(req);
    }

    @VerifyUserToken
    @PostMapping("/chatbot/modify_greet_status")
    @ApiOperation(value = "修改打招呼的状态",notes = "修改打招呼的状态", httpMethod = "POST", response = ResultVO.class)
    public ResultVO modifyGreetStatus(@RequestBody @Valid UpdateGreetConfigStatusReq req) {
        if (Objects.isNull(req) || Objects.isNull(req.getIsGreetOn())) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyGreetStatus(req);
    }


    @ApiOperation("修改复聊任务的状态")
    @VerifyUserToken
    @PostMapping("/chatbot/modify_rechat_status")
    public ResultVO modifyReChatStatus (@RequestBody @Valid UpdateGreetConfigStatusReq req) {

        if (Objects.isNull(req) || Objects.isNull(req.getIsRechatOn())) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyReChatStatus(req);
    }



    @ApiOperation("修改AI跟进任务的状态")
    @VerifyUserToken
    @PostMapping("/chatbot/modify_ai_status")
    public ResultVO modifyAiStatus (@RequestBody @Valid UpdateGreetConfigStatusReq req) {

        if (Objects.isNull(req) || Objects.isNull(req.getIsAiOn())) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.modifyAIOnStatus(req);
    }


    @ApiOperation("设置打招呼筛选条件")
    @VerifyUserToken
    @PostMapping("/chatbot/set_greet_condition")
    public ResultVO setGreetCondition (@RequestBody @Valid AddOrUpdateChatbotGreetCondition req) {

        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.setGreetCondition(req);
    }





    @ApiOperation("添加/编辑任务")
    @VerifyUserToken
    @PostMapping("/chatbot/set_greet_task")
    public ResultVO<AmChatbotGreetTask> setGreetTask (@RequestBody @Valid AddOrUpdateAmChatbotGreetTask req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.setGreetTask(req);
    }





    @ApiOperation("获取打招呼任务列表")
    @VerifyUserToken
    @PostMapping("/chatbot/get_greet_tasks")
    public ResultVO<List<AmChatbotGreetTaskVo>> getGreetTasks (@RequestBody @Valid SearchAmChatbotGreetTask req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotManager.getGreetTasks(req);
    }

    @ApiOperation("删除打招呼任务")
    @VerifyUserToken
    @GetMapping("/chatbot/delete_task")
    public ResultVO deleteGreetTask(@RequestParam(value = "id",required = true) Integer id) {
        return chatBotManager.deleteGreetTask(id);
    }



    @ApiOperation("根据职位id获取筛选条件")
    @VerifyUserToken
    @GetMapping("/chatbot/get_condition_by_position_id")
    public ResultVO<AmChatbotGreetCondition> getConditionByPositionId(@RequestParam(value = "id",required = true) Integer id) {
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
    public  ResultVO<AmChatbotPositionOption> setPositionOption(@RequestBody @Valid AddPositionOptions req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        if (Objects.isNull(req.getSquareRoleId() ) && Objects.isNull(req.getRechatOptionId())) {
            return ResultVO.fail("打招呼方案跟复聊方案不能同时为空");
        }
        return chatBotManager.setPositionOption(req);
    }


}
