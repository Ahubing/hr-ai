package com.open.ai.eros.ai.model.processor.before;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.vo.AIParamVo;
import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.processor.AIChatBeforeProcessor;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.constants.ModelPriceEnum;
import com.open.ai.eros.common.util.RegexUtil;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.ConversationTypeEnum;
import com.open.ai.eros.db.mysql.ai.entity.ChatConversation;
import com.open.ai.eros.db.mysql.ai.entity.ChatMessage;
import com.open.ai.eros.db.mysql.ai.service.impl.ChatConversationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;


/**
 * 保存用户的对话
 */
@Order(30)
@Component
@Slf4j
public class ChatConvertBeforeProcessor implements AIChatBeforeProcessor {


    @Autowired
    private ChatConversationServiceImpl chatConversationService;


    @Override
    public ResultVO<Void> aiChatBefore(AITextChatVo chatReq, Long userId, SendMessageUtil sendMessageUtil) {

        if(StringUtils.isEmpty(chatReq.getConversationId())){
            return ResultVO.success();
        }

        if(CollectionUtils.isNotEmpty(chatReq.getMessages())){
            // 如果消息由前端传入的话 直接返回 一般是给api使用
            return ResultVO.success();
        }

        ChatConversation conversation = chatConversationService.getById(chatReq.getConversationId());
        if(conversation==null){
            return ResultVO.fail("会话信息不存在");
        }
        chatReq.setConversationType(conversation.getType());

        if(Objects.equals(ConversationTypeEnum.MASK.getType(), conversation.getType())){
            chatReq.setMaskId(conversation.getMaskId());
            return ResultVO.success();
        } else if (ConversationTypeEnum.KNOWLEDGE.getType().equals(conversation.getType())) {
            chatReq.setKnowledgeId(conversation.getKnowledgeId());
        }

        String aiParam = conversation.getAiParam();
        if(StringUtils.isEmpty(aiParam)){
            return ResultVO.success();
        }
        AIParamVo paramVo = JSONObject.parseObject(aiParam, AIParamVo.class);
        chatReq.setContentNumber(paramVo.getContentNumber());
        chatReq.setMessages(paramVo.getMessages());

        if(paramVo.getMinScore()==null){
            chatReq.setMinScore(0.6);
        }else{
            chatReq.setMinScore(paramVo.getMinScore());
        }
        chatReq.setUserSuffix(paramVo.getUserPrefix());
        chatReq.setUserSuffix(paramVo.getUserSuffix());

        return ResultVO.success();
    }


    private com.open.ai.eros.common.vo.ChatMessage convertChatMessage(ChatMessage chatMessage,String model){
        com.open.ai.eros.common.vo.ChatMessage chatMessage1 = new com.open.ai.eros.common.vo.ChatMessage(chatMessage.getRole(), chatMessage.getContent());
        buildVersionModelMessage(chatMessage1,model);
        return chatMessage1;
    }


    static HashSet<String> versionSet = new HashSet<>();
    static {
        versionSet.add(ModelPriceEnum.gpt_4_vision_preview.getModel());
        versionSet.add(ModelPriceEnum.gpt_4o.getModel());
        versionSet.add(ModelPriceEnum.gpt_4o_2024_05_13.getModel());
    }


    private void buildVersionModelMessage(com.open.ai.eros.common.vo.ChatMessage chatBaseMessage,String model){
        if(!versionSet.contains(model)){
            return ;
        }
        Object messageContent = chatBaseMessage.getContent();
        if(messageContent instanceof ArrayList){
            return;
        }
        String content = messageContent.toString();
        boolean versionFormat = RegexUtil.validateVersionFormat(content);
        if(versionFormat){
            try {
                String[] contents = content.split("\\s", 2);
                String url = contents[0];
                String prompt = contents[1];
                ////			// 创建文本内容的JSON对象
                JSONArray jsonArray = new JSONArray();
                JSONObject textObject = new JSONObject();
                textObject.put("type", "text");
                textObject.put("text", prompt);

                // 创建图片URL的JSON对象
                JSONObject imageObject = new JSONObject();
                imageObject.put("type", "image_url");

                JSONObject urlObject = new JSONObject();
                urlObject.put("url", url);
                imageObject.put("image_url", urlObject);

                // 将JSON对象添加到JSON数组
                jsonArray.add(textObject);
                jsonArray.add(imageObject);
                // 输出JSON字符串
                chatBaseMessage.setContent(jsonArray);
            }catch (Exception e){
                log.error("buildVersionModelMessage error chatBaseMessage={}",JSONObject.toJSONString(chatBaseMessage),e);
            }
        }
    }


}
