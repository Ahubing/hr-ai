package com.open.ai.eros.ai.controller;

import com.open.ai.eros.ai.bean.req.AddChatConversationReq;
import com.open.ai.eros.ai.bean.req.UpdateChatConversationReq;
import com.open.ai.eros.ai.bean.vo.ChatConversationResultVo;
import com.open.ai.eros.ai.bean.vo.ChatConversationVo;
import com.open.ai.eros.ai.config.AIBaseController;
import com.open.ai.eros.ai.manager.ConversionManager;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * @类名：ConversionController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 20:16
 */
@Api(tags = "AI会话控制类")
@RestController
@Slf4j
public class ConversionController extends AIBaseController {


    @Autowired
    private ConversionManager conversionManager;


    /**
     * 获取用户的会话列表
     *
     * @return
     */
    @ApiOperation(value = "获取用户的会话列表")
    @VerifyUserToken
    @GetMapping("/chat/conversation/list")
    public ResultVO<ChatConversationResultVo> getList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return conversionManager.getList(getUserId(), pageNum, pageSize);
    }


    @ApiOperation(value = "获取用户的会话信息")
    @VerifyUserToken
    @GetMapping("/chat/conversation/id")
    public ResultVO<ChatConversationVo> getChatConversationById(@RequestParam("id") String id) {
        return conversionManager.getChatConversationById(id, getUserId());
    }


    /**
     * 更新某个会话消息为已读
     *
     * @return
     */
    @ApiOperation(value = "更新某个会话消息为已读")
    @VerifyUserToken
    @GetMapping("/chat/conversation/update/readStatus")
    public ResultVO updateMessageReadStatus(@RequestParam(value = "id", required = true) String id) {
        return conversionManager.updateMessageReadStatus(getUserId(), id);
    }


    /**
     * 删除单个会话信息
     *
     * @return
     */
    @ApiOperation(value = "删除会话")
    @VerifyUserToken
    @GetMapping("/chat/conversation/delete")
    public ResultVO delete(@RequestParam(value = "id", required = true) String id) {
        return conversionManager.deleteChatConversation(id, getUserId());
    }


    /**
     * 根据会话id删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "清空会话消息")
    @VerifyUserToken
    @GetMapping("/conversation/chat/message/clear")
    public ResultVO clearChatMessage(@RequestParam(value = "id", required = true) String id, @RequestParam(value = "chatId", required = false) Long chatId) {
        return conversionManager.clearChatMessage(id, chatId, getUserId());
    }


    /**
     * 修改单个会话信息
     *
     * @return
     */
    @ApiOperation(value = "修改会话")
    @VerifyUserToken(required = false)
    @PostMapping("/chat/conversation/update")
    public ResultVO update(@RequestBody @Valid UpdateChatConversationReq req) {
        return conversionManager.updateChatConversation(req, getUserId());
    }


    /**
     * 新增单个会话信息
     *
     * @return
     */
    @ApiOperation(value = "新增会话")
    @VerifyUserToken
    @PostMapping("/chat/conversation/add")
    public ResultVO<Map<String, String>> addConversation(@RequestBody @Valid AddChatConversationReq req) {
        return conversionManager.addChatConversation(req, getUserId());
    }

}
