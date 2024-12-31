package com.open.ai.eros.ai.model.processor.before;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.bean.vo.SearchKnowledgeResult;
import com.open.ai.eros.ai.manager.EmbeddingSearchService;
import com.open.ai.eros.ai.model.processor.AIChatBeforeProcessor;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import com.open.ai.eros.db.constants.ConversationTypeEnum;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.constants.MaskEnum;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @类名：MaskBeforeProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/24 12:37
 */

@Order(60)
@Component
@Slf4j
public class KnowledgeProcessor implements AIChatBeforeProcessor {


    @Autowired
    private KnowledgeServiceImpl knowledgeService;

    @Autowired
    private EmbeddingSearchService embeddingSearchService;


    @Override
    public ResultVO<Void> aiChatBefore(AITextChatVo chatReq, Long userId, SendMessageUtil sendMessageUtil) {

        BMaskVo bMaskVo = chatReq.getBMaskVo();

        Long knowledgeId = null;

        if(chatReq.getConversationType().equals(ConversationTypeEnum.KNOWLEDGE.getType())){
            knowledgeId = chatReq.getKnowledgeId();
        }else{
            if(bMaskVo==null || !MaskEnum.KNOWLEDGE_MASK.getType().equals(bMaskVo.getType())  || bMaskVo.getKnowledgeId()==null){
                return ResultVO.success();
            }
            knowledgeId = bMaskVo.getKnowledgeId();
        }
        if(knowledgeId==null){
            return ResultVO.success();
        }
        Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
        if(knowledge==null){
            return ResultVO.success();
        }
        String collectionName = String.format(KnowledgeConstant.knowledgeName, knowledge.getId());
        LinkedList<ChatMessage> messages = chatReq.getMessages();
        // 当前的用户消息
        ChatMessage chatMessage = messages.getLast();
        String text = chatMessage.getContent().toString();
        log.info("开始知识问答！knowledgeName={}text={}",knowledge.getName(),text);
        try {
            ResultVO<SearchKnowledgeResult> searchResult = embeddingSearchService.searchKnowledgeContent(knowledgeId, text,3,chatReq.getMinScore()==null?0.6:chatReq.getMinScore());
            if(!searchResult.isOk() || CollectionUtils.isEmpty(searchResult.getData().getContents())){
                if(bMaskVo!=null && bMaskVo.getKnowledgeStrict()==1){
                    sendMessageUtil.sendMessage(sendMessageUtil.formatAIStr("不好意思,已经超出我的知识范围，暂时不能回答您,十分抱歉！",false));
                    return ResultVO.fail();
                }
                return ResultVO.success();
            }
            List<String> dataList = searchResult.getData().getContents();
            StringBuilder newMessage = new StringBuilder("根据已知内容以专业、通俗易懂的方式来回答问题\n");
            for (int i = 0; i < dataList.size(); i++) {
                newMessage.append("已知内容").append(i + 1).append(": ").append(dataList.get(i)).append("\n");
            }
            newMessage.append("问题: ").append(chatMessage.getContent().toString());
            chatMessage.setContent(newMessage.toString());
            chatReq.setDocsSources(searchResult.getData().getSource());
        }catch (Exception e){
            log.error("KnowledgeProcessor error text={} collectionName={}",text,collectionName,e);
        }
        return ResultVO.success();
    }

}
