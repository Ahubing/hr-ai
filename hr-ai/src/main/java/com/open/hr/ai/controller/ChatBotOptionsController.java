package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.req.AddOrUpdateAmChatbotOptions;
import com.open.hr.ai.bean.req.AddOrUpdateAmChatbotOptionsItems;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.ChatBotOptionsManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * @Date 2025/1/4 23:19
 */

@Api(tags = "ChatBot Options管理类")
@Slf4j
@RestController
public class ChatBotOptionsController extends HrAIBaseController {

    @Resource
    private ChatBotOptionsManager chatBotOptionsManager;

    @ApiOperation("获取方案列表")
    @VerifyUserToken
    @GetMapping("chatbotoptions/list")
    private ResultVO chatBotOptionsList(@RequestParam(value = "type",required = true) Integer type,@RequestParam(value = "keyword",required = false) String keyword) {
        Long adminId = getUserId();
        return chatBotOptionsManager.chatbotOptionsList(adminId,type,keyword);
    }


    @ApiOperation("获取方案详情")
    @VerifyUserToken
    @GetMapping("chatbotoptions/detail")
    private ResultVO chatBotOptionsDetail(@RequestParam(value = "id",required = true) Integer id) {
        return chatBotOptionsManager.detail(id);
    }


    @ApiOperation("新增/编辑方案")
    @VerifyUserToken
    @PostMapping("chatbotoptions/edit")
    private ResultVO chatBotOptionsEdit(@RequestBody @Valid AddOrUpdateAmChatbotOptions req) {
        if (Objects.isNull(req)){
            return ResultVO.fail("参数不能为空");
        }
        return chatBotOptionsManager.edit(req);
    }

    @ApiOperation("新增/编辑方案执行话术项目")
    @VerifyUserToken
    @PostMapping("chatbotoptions/edit_item")
    private ResultVO chatBotOptionsEditItems(@RequestBody @Valid AddOrUpdateAmChatbotOptionsItems req) {
        if (Objects.isNull(req)){
            return ResultVO.fail("参数不能为空");
        }
        return chatBotOptionsManager.editItems(req);
    }


    @ApiOperation("获取AI角色列表")
    @VerifyUserToken
    @GetMapping("chatbotoptions/get_ai_roles")
    private ResultVO getAiRoles() {
        return chatBotOptionsManager.getAiRoles();
    }


    @ApiOperation("删除方案")
    @VerifyUserToken
    @GetMapping("chatbotoptions/delete")
    private ResultVO delete(@RequestParam(value = "id",required = true) Integer id) {
        return chatBotOptionsManager.deleteOptions(id);
    }


    @ApiOperation("删除话术项目")
    @VerifyUserToken
    @GetMapping("chatbotoptions/delete_item")
    private ResultVO deleteItem(@RequestParam(value = "id",required = true) Integer id) {
        return chatBotOptionsManager.deleteOptionsItem(id);
    }


}
