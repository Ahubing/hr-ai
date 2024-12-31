package com.open.ai.eros.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.ai.bean.req.AddChatConversationReq;
import com.open.ai.eros.ai.bean.req.UpdateChatConversationReq;
import com.open.ai.eros.ai.bean.vo.ChatConversationResultVo;
import com.open.ai.eros.ai.bean.vo.ChatConversationVo;
import com.open.ai.eros.ai.bean.vo.ChatMessageVo;
import com.open.ai.eros.ai.convert.ChatConvert;
import com.open.ai.eros.common.constants.ChatSourceEnum;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import com.open.ai.eros.creator.manager.MaskManager;
import com.open.ai.eros.db.constants.CommonStatusEnum;
import com.open.ai.eros.db.mysql.ai.entity.ChatConversation;
import com.open.ai.eros.db.mysql.ai.entity.ChatMessage;
import com.open.ai.eros.db.mysql.ai.service.impl.ChatConversationServiceImpl;
import com.open.ai.eros.db.mysql.ai.service.impl.ChatMessageServiceImpl;
import com.open.ai.eros.db.mysql.creator.entity.Mask;
import com.open.ai.eros.db.mysql.user.entity.UserFollowMask;
import com.open.ai.eros.db.mysql.user.service.impl.UserFollowMaskServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @类名：ConversionManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 20:19
 */

@Component
@Slf4j
public class ConversionManager {


    @Autowired
    private ChatConversationServiceImpl chatConversationService;

    @Autowired
    private MaskManager maskManager;

    @Autowired
    private ChatMessageServiceImpl chatMessageService;

    @Autowired
    private UserFollowMaskServiceImpl userFollowMaskService;


    public ResultVO updateMessageReadStatus(Long userId, String conversionId) {
        Date endTime = new Date();
        Date startTime = DateUtils.getDayAfter(endTime, -10);
        int unReadChatMessage = chatMessageService.getUnReadChatMessage(conversionId, userId, startTime, endTime);
        log.info("updateMessageReadStatus conversionId={},unReadChatMessage={}", conversionId, unReadChatMessage);
        return ResultVO.success();
    }


    public ResultVO<ChatConversationVo> getChatConversationById(String id, Long userId) {
        ChatConversation conversation = chatConversationService.getById(id);
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            return ResultVO.fail("会话信息不存在！");
        }

        return ResultVO.success(ChatConvert.I.convertChatConversationVo(conversation));
    }


    /**
     * 获取用户最近的100条对话
     *
     * @return
     */
    public ResultVO<ChatConversationResultVo> getList(Long userId, Integer pageNum, Integer pageSize) {

        ChatConversationResultVo resultVo = new ChatConversationResultVo();
        resultVo.setChatConversationVos(new ArrayList<>());

        LambdaQueryWrapper<ChatConversation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ChatConversation::getUserId, userId);
        lambdaQueryWrapper.orderByDesc(ChatConversation::getCreateTime);

        Page<ChatConversation> page = new Page<>(pageNum, pageSize);

        Page<ChatConversation> conversationPage = chatConversationService.page(page, lambdaQueryWrapper);
        List<ChatConversation> records = conversationPage.getRecords();
        if (records.size() < pageSize) {
            resultVo.setLastPage(true);
        }

        Date endTime = new Date();
        Date startTime = DateUtils.getDayAfter(endTime, -10);

        List<Long> maskIds = records.stream()
                .map(ChatConversation::getMaskId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Mask> masks = maskManager.batchGetCacheMask(maskIds);
        Map<Long, Optional<Mask>> maskMap = masks.stream().collect(Collectors.toMap(Mask::getId, Optional::of, (k1, k2) -> k1));

        List<ChatConversationVo> conversations = new ArrayList<>();
        for (ChatConversation chatConversation : records) {
            if (chatConversation.getMaskId() != null && maskMap.get(chatConversation.getMaskId()) == null) {
                continue;
            }
            ChatConversationVo chatConversationVo = ChatConvert.I.convertChatConversationVo(chatConversation);

            if (userId != null && chatConversation.getMaskId() != null) {
                UserFollowMask userFollowMask = userFollowMaskService.getUserFollowMask(userId, chatConversation.getMaskId());
                if (userFollowMask != null) {
                    chatConversationVo.setFollow(true);
                }
            }

            ChatMessage newMessage = chatMessageService.getNewMessage(chatConversationVo.getId(), userId);
            if (newMessage != null) {
                String content = newMessage.getContent();
                content = content.length() > 100 ? content.substring(0, 100) : content;
                // 防止换行
                content = content.replace("\n", "");
                chatConversationVo.setChatMessage(ChatMessageVo.builder().content(content).role(newMessage.getRole()).build());
                String time = ChatConvert.I.getTime(newMessage.getCreateTime());
                chatConversationVo.setCreateTime(time);
            }
            int unReadChatMessageCount = chatMessageService.getUnReadChatMessage(chatConversationVo.getId(), userId, startTime, endTime);
            chatConversationVo.setUnReadChatMessageCount(unReadChatMessageCount);
            conversations.add(chatConversationVo);
        }
        resultVo.setChatConversationVos(conversations);
        return ResultVO.success(resultVo);
    }


    /**
     * 删除对话信息
     *
     * @param chatConversationId
     * @return
     */
    public ResultVO deleteChatConversation(String chatConversationId, Long userId) {
        int result = chatConversationService.deleteChatConversation(userId, chatConversationId);
        log.info("deleteChatConversation chatConversationId={},account={} result={} ", chatConversationId, userId, result);
        return result > 0 ? ResultVO.success() : ResultVO.fail("删除失败！");
    }


    /**
     * 清除会话消息
     *
     * @param chatConversationId
     * @param userId
     * @return
     */
    public ResultVO clearChatMessage(String chatConversationId, Long chatId, Long userId) {
        int result = chatMessageService.updateMessageStatus(chatConversationId, userId, chatId, CommonStatusEnum.DELETE.getStatus());
        log.info("clearChatMessage chatConversationId={} result={}", chatConversationId, result);
        return ResultVO.success();
    }


    /**
     * 修改对话信息
     *
     * @return
     */
    public ResultVO updateChatConversation(UpdateChatConversationReq req, Long userId) {

        ChatConversation conversation = chatConversationService.getById(req.getId());
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            return ResultVO.fail("会话不存在");
        }
        conversation.setType(req.getType());
        conversation.setMaskId(req.getMaskId());
        conversation.setName(req.getName());
        conversation.setKnowledgeId(req.getKnowledgeId());
        if (req.getParamVo() != null) {
            LinkedList<com.open.ai.eros.common.vo.ChatMessage> messages = req.getParamVo().getMessages();
            LinkedList<com.open.ai.eros.common.vo.ChatMessage> newMessages = new LinkedList<>();
            for (com.open.ai.eros.common.vo.ChatMessage message : messages) {
                if(message.getContent()==null || StringUtils.isEmpty(message.getContent().toString())){
                    continue;
                }
                newMessages.add(message);
            }
            req.getParamVo().setMessages(newMessages);
            conversation.setAiParam(JSONObject.toJSONString(req.getParamVo()));
        }
        boolean updated = chatConversationService.updateById(conversation);
        log.info("updateChatConversation chatConversationId={},account={} updated={} ", req.getId(), userId, updated);
        return updated ? ResultVO.success() : ResultVO.fail("修改失败！");
    }


    /**
     * 新增对话信息
     *
     * @return
     */
    public ResultVO<Map<String, String>> addChatConversation(AddChatConversationReq req, Long userId) {
        ChatConversation chatConversation;
        Map<String, String> conversationIdMap = new HashMap<>();
        Long maskId = req.getMaskId();
        if (maskId != null && !ChatSourceEnum.MASK.name().equals(req.getSource())) {
            BMaskVo maskVo = maskManager.getCacheBMaskById(maskId);
            if (maskVo == null) {
                return ResultVO.fail("面具不存在");
            }
            chatConversation = chatConversationService.getChatConversationByUserAndMaskId(userId, maskId);
            if (chatConversation != null) {
                conversationIdMap.put("conversationId", chatConversation.getId());
                return ResultVO.success(conversationIdMap);
            }
            chatConversation = new ChatConversation();
            chatConversation.setName(maskVo.getName());
            chatConversation.setAvatar(maskVo.getAvatar());
        } else {
            chatConversation = new ChatConversation();
            chatConversation.setName(req.getName());
            chatConversation.setAvatar(req.getAvatar());
        }
        chatConversation.setType(req.getType());
        chatConversation.setUserId(userId);
        chatConversation.setCreateTime(LocalDateTime.now());
        chatConversation.setMaskId(maskId);
        chatConversation.setShareMaskId(req.getShareMaskId());
        chatConversation.setKnowledgeId(req.getKnowledgeId());
        if (req.getAiParamVo() != null) {
            LinkedList<com.open.ai.eros.common.vo.ChatMessage> messages = req.getAiParamVo().getMessages();
            LinkedList<com.open.ai.eros.common.vo.ChatMessage> newMessages = new LinkedList<>();
            for (com.open.ai.eros.common.vo.ChatMessage message : messages) {
                if(message.getContent()==null || StringUtils.isEmpty(message.getContent().toString())){
                    continue;
                }
                newMessages.add(message);
            }
            req.getAiParamVo().setMessages(newMessages);
            chatConversation.setAiParam(JSONObject.toJSONString(req.getAiParamVo()));
        }

        boolean saveResult = chatConversationService.save(chatConversation);
        log.info("addChatConversation req={},account={} result={} ", JSONObject.toJSONString(req), userId, saveResult);

        if (!saveResult) {
            return ResultVO.fail("保存失败！");
        }

        conversationIdMap.put("conversationId", chatConversation.getId());
        return ResultVO.success(conversationIdMap);
    }


}
