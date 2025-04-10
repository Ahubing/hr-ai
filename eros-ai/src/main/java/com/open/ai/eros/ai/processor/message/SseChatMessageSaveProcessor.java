package com.open.ai.eros.ai.processor.message;

import com.open.ai.eros.ai.bean.req.AddChatMessageReq;
import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.bean.vo.MaskSseConversationVo;
import com.open.ai.eros.ai.manager.AIManager;
import com.open.ai.eros.ai.manager.ThreadPoolManager;
import com.open.ai.eros.ai.processor.ChatMessageSaveProcessor;
import com.open.ai.eros.ai.processor.message.bean.ChatMessageSaveParam;
import com.open.ai.eros.ai.util.MaskChannelSseEmitterUtils;
import com.open.ai.eros.ai.util.MaskSseEmitterUtils;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.constants.ChatSourceEnum;
import com.open.ai.eros.common.exception.AIException;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.ai.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;

/**
 * 用于分析当前用户的prompt
 */
@Slf4j
@Order(20)
@Component
public class SseChatMessageSaveProcessor implements ChatMessageSaveProcessor {


    @Autowired
    private AIManager aiManager;

    @Autowired
    private MaskSseEmitterUtils maskSseEmitterUtils;

    @Autowired
    private MaskChannelSseEmitterUtils maskChannelSseEmitterUtils;


    /**
     * 是否发起ai
     *
     * @param req
     * @return
     */
    private boolean startAI(AddChatMessageReq req) {
        String source = req.getSource();
        if (ChatSourceEnum.MASK_DISCUSS.name().equals(source)) {
            return false;
        }
        return true;
    }


    @Override
    public ResultVO after(ChatMessageSaveParam param, HttpServletResponse response) {
        AddChatMessageReq req = param.getReq();

        if (!startAI(req)) {
            return ResultVO.success();
        }

        ChatMessage chatMessage = param.getChatMessage();

        String conversationId = req.getConversationId();

        String templateModel = req.getTemplateModel();
        String[] split = templateModel.split(":");
        if (split.length != 2) {
            throw new AIException("传入的模型标识有问题");
        }
        AITextChatVo aiTextChatVo = AITextChatVo.builder()
                .chatId(chatMessage.getId())
                .template(split[0])
                .model(split[1])
                .shareMaskId(req.getShareMaskId())
                .conversationId(req.getConversationId())
                .maskId(req.getMaskId())
                .stream(true)
                .build();
        try {
            CacheUserInfoVo cacheUserInfo = param.getCacheUserInfo();
            String source = req.getSource();
            SendMessageUtil sendMessageUtil = new SendMessageUtil();
            if (ChatSourceEnum.MASK.name().equals(source) || StringUtils.isEmpty(source)) {
                MaskSseConversationVo maskSseConversationVo = maskSseEmitterUtils.getSseEmitter(conversationId);
                if (maskSseConversationVo != null) {
                    sendMessageUtil.setResponse(response);
                    sendMessageUtil.setMaskSseConversationVo(maskSseConversationVo);
                }
            } else if (ChatSourceEnum.CHANNEL_CHAT.name().equals(source)) {
                SseEmitter connect = maskChannelSseEmitterUtils.getConnect(req.getMaskId(), cacheUserInfo.getId());
            }

            if (sendMessageUtil.getMaskSseConversationVo() == null) {
                SendMessageUtil sendMessageUtil1 = new SendMessageUtil(response);
                sendMessageUtil1.setSource("chat");
                aiManager.startAIChat(cacheUserInfo, aiTextChatVo, sendMessageUtil1);
                return ResultVO.success();
            }

            ThreadPoolManager.sseChatPool.execute(() -> {
                try {
                    aiManager.startAIChat(cacheUserInfo, aiTextChatVo, sendMessageUtil);
                } catch (Exception e) {
                    log.error("aiSseTextChat error ", e);
                }
            });

        } catch (Exception e) {
            log.error("SseChatMessageSaveProcessor error ", e);
        }
        return ResultVO.success();
    }
}
