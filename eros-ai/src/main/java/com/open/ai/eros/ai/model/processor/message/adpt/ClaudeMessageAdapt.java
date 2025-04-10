//package com.open.ai.eros.ai.model.processor.message.adpt;
//
//import com.open.ai.eros.ai.bean.vo.AITextChatVo;
//import com.open.ai.eros.ai.model.ModelMessageAdapt;
//import com.open.ai.eros.ai.util.ChatMessageCacheUtil;
//import com.open.ai.eros.common.vo.ChatMessage;
//import com.open.ai.eros.creator.bean.vo.MaskAIParamVo;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.util.LinkedList;
//
///**
// * @类名：GptMessageAdapt
// * @项目名：web-eros-ai
// * @description：
// * @创建人：陈臣
// * @创建时间：2024/8/8 22:08
// */
//
//@Order(30)
//@Component
//public class ClaudeMessageAdapt implements ModelMessageAdapt {
//
//
//    @Override
//    public String modelMessage(AITextChatVo req, MaskAIParamVo maskAIParamVo, String model) {
//        LinkedList<ChatMessage> userHistoryMessages = req.getMessages();
//        ChatMessageCacheUtil.getOkUserChatMessages(userHistoryMessages, model);
//
//        return null;
//    }
//
//    @Override
//    public boolean match(String model) {
//        return model.contains("claude");
//    }
//}
