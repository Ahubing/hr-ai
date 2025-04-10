package com.open.ai.eros.ai.controller;

import com.open.ai.eros.ai.bean.req.AITextChatReq;
import com.open.ai.eros.ai.bean.req.TestMaskReq;
import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.bean.vo.MaskSseConversationVo;
import com.open.ai.eros.ai.config.AIBaseController;
import com.open.ai.eros.ai.manager.AIManager;
import com.open.ai.eros.ai.manager.ThreadPoolManager;
import com.open.ai.eros.ai.util.MaskSseEmitterUtils;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.exception.AIException;
import com.open.ai.eros.common.exception.SeeConnectException;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @类名：AITextChatController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/8 21:05
 */

@Api(tags = "ai文本类聊天")
@Slf4j
@RestController
public class AITextChatController extends AIBaseController {


    @Autowired
    private AIManager aiManager;


    @Autowired
    private MaskSseEmitterUtils maskSseEmitterUtils;


    /**
     * sse 订阅消息
     */
    @ApiOperation("订阅面具sse")
    @VerifyUserToken
    @GetMapping(path = "/sub/mask", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter sub(@RequestParam(value = "conversationId") String conversationId) throws IOException {
        maskSseEmitterUtils.connect(conversationId);

        MaskSseConversationVo sseConversationVo = maskSseEmitterUtils.getSseEmitter(conversationId);
        if (sseConversationVo != null) {
            SseEmitter sseEmitter = sseConversationVo.getSseEmitter();
            sseEmitter.send(SendMessageUtil.EROS_CONNECT_SUCCESS);
            return sseEmitter;
        }
        return null;
    }


    /**
     * sse 订阅消息
     */
    @ApiOperation("面具心跳")
    @VerifyUserToken
    @GetMapping(path = "/sub/mask/heart")
    public ResultVO heart(@RequestParam(value = "conversationId") String conversationId, @RequestParam(value = "time") Long time) throws IOException {
        MaskSseConversationVo sseConversationVo = maskSseEmitterUtils.getSseEmitter(conversationId);
        if (sseConversationVo != null) {
            long lastVisitTime = sseConversationVo.getConnectTime();
            long now = System.currentTimeMillis();
            SseEmitter sseEmitter = sseConversationVo.getSseEmitter();
            sseEmitter.send(SendMessageUtil.HEART);
            if (now - lastVisitTime > MaskSseEmitterUtils.timeOut / 2) {
                return ResultVO.fail();
            }
            return ResultVO.success();
        }
        return ResultVO.fail();
    }


    /**
     * 文本聊天
     *
     * @param req
     */
    //@VerifyUserToken
    //@ApiOperation("sse文本ai聊天")
    //@PostMapping(value = {"/v1/sse/chat/completions"})
    public void aiSseTextChat(@RequestBody @Valid AITextChatReq req, HttpServletResponse response) {
        CacheUserInfoVo cacheUserInfo = getCacheUserInfo();
        if (cacheUserInfo == null || cacheUserInfo.getId() == null) {
            throw new AIException("获取用户信息错误！");
        }
        String templateModel = req.getTemplateModel();
        String[] split = templateModel.split(":");
        if (split.length != 2) {
            throw new AIException("传入的模型标识有问题");
        }
        AITextChatVo aiTextChatVo = AITextChatVo.builder()
                .chatId(req.getChatId())
                .template(split[0])
                .model(split[1])
                .shareMaskId(req.getShareMaskId())
                .conversationId(req.getConversationId())
                .maskId(req.getMaskId())
                .build();
        try {
            MaskSseConversationVo sseEmitter = maskSseEmitterUtils.getSseEmitter(req.getConversationId());
            if (sseEmitter == null || sseEmitter.getSseEmitter() == null) {
                throw new SeeConnectException("连接丢失！");
            }
            SendMessageUtil sendMessageUtil = new SendMessageUtil(response, new MaskSseConversationVo(sseEmitter.getSseEmitter()));
            ThreadPoolManager.sseChatPool.execute(() -> {
                try {
                    aiManager.startAIChat(cacheUserInfo, aiTextChatVo, sendMessageUtil);
                } catch (Exception e) {
                    log.error("aiSseTextChat error ", e);
                }
            });
        } catch (AIException e) {
            throw e;
        } catch (Exception e) {
            log.error("aiSseTextChat error ", e);
        }
    }


    /**
     * 文本聊天
     *
     * @param req
     */
    @VerifyUserToken
    @ApiOperation("文本ai聊天")
    @PostMapping(value = {"/v1/chat/completions"})
    public void aiTextChat(@RequestBody @Valid AITextChatReq req, HttpServletResponse response) {
        CacheUserInfoVo cacheUserInfo = getCacheUserInfo();
        if (cacheUserInfo == null || cacheUserInfo.getId() == null) {
            throw new AIException("获取用户信息错误！");
        }

        String templateModel = req.getTemplateModel();
        String[] split = templateModel.split(":");
        if (split.length != 2) {
            throw new AIException("传入的模型标识有问题");
        }

        AITextChatVo aiTextChatVo = AITextChatVo.builder()
                .chatId(req.getChatId())
                .template(split[0])
                .model(split[1])
                .shareMaskId(req.getShareMaskId())
                .conversationId(req.getConversationId())
                .maskId(req.getMaskId())
                .build();
        try {
            SendMessageUtil sendMessageUtil = new SendMessageUtil(response);
            aiManager.startAIChat(cacheUserInfo, aiTextChatVo, sendMessageUtil);
        } catch (AIException e) {
            throw e;
        } catch (Exception e) {
            log.error("aiTextChat error ", e);
        }
    }


    /**
     * 面具测试
     *
     * @param req
     */
    @VerifyUserToken(role = {RoleEnum.CREATOR, RoleEnum.SYSTEM})
    @ApiOperation("面具测试")
    @PostMapping(value = {"/v1/mask/text/chat/completions"})
    public void maskTestTextChat(@RequestBody @Valid TestMaskReq req, HttpServletResponse response) {
        CacheUserInfoVo cacheUserInfo = getCacheUserInfo();
        if (cacheUserInfo == null || cacheUserInfo.getId() == null) {
            throw new AIException("获取用户信息错误！");
        }

        if (req.getTemplateModel() == null) {
            req.setTemplateModel(req.getMaskVo().getTemplateModel().get(0));
        } else if (!req.getMaskVo().getTemplateModel().contains(req.getTemplateModel())) {
            throw new AIException("该面具不支持该类型的模型访问！");
        }

        String templateModel = req.getTemplateModel();
        String[] split = templateModel.split(":");
        if (split.length != 2) {
            throw new AIException("传入的模型标识有问题");
        }

        LinkedList<ChatMessage> messages = req.getMessages();
        if (CollectionUtils.isEmpty(messages)) {
            messages = new LinkedList<>();
            req.setMessages(messages);
        }
        messages.add(new ChatMessage(AIRoleEnum.USER.name(), req.getUserPrompt()));
        req.setUserPrompt(req.getUserPrompt());

        AITextChatVo aiTextChatVo = AITextChatVo.builder()
                .bMaskVo(req.getMaskVo())
                .template(split[0])
                .model(split[1])
                .messages(req.getMessages())
                .stream(true)
                .build();
        try {
            SendMessageUtil sendMessageUtil = new SendMessageUtil(response);
            aiManager.testMask(cacheUserInfo, aiTextChatVo, sendMessageUtil);
        } catch (AIException e) {
            throw e;
        } catch (Exception e) {
            log.error("maskTestTextChat error ", e);
        }
    }


}
