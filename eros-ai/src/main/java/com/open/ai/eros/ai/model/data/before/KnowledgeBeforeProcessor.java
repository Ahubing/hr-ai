package com.open.ai.eros.ai.model.data.before;

import com.open.ai.eros.ai.bean.dto.AITextRequest;
import com.open.ai.eros.ai.bean.dto.AITextResponse;
import com.open.ai.eros.ai.bean.vo.SearchKnowledgeResult;
import com.open.ai.eros.ai.manager.EmbeddingSearchService;
import com.open.ai.eros.ai.model.data.DataAfterProcessor;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @类名：AIToolBeforeProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/12/9 15:42
 */

/**
 *
 */
@Component
@Slf4j
public class KnowledgeBeforeProcessor implements DataAfterProcessor {

    @Autowired
    private KnowledgeServiceImpl knowledgeService;

    @Autowired
    private EmbeddingSearchService embeddingSearchService;



    @Override
    public ResultVO after(AITextRequest request, AITextResponse response) {


        Long knowledgeId = request.getKnowledgeId();
        if(knowledgeId==null){
            return ResultVO.success();
        }

        Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
        if(knowledge==null){
            log.error("knowledge is null knowledgeId={}",knowledgeId);
            return ResultVO.success();
        }

        LinkedList<ChatMessage> messages = request.getMessages();
        String collectionName = String.format(KnowledgeConstant.knowledgeName, knowledge.getId());
        // 当前的用户消息
        ChatMessage chatMessage = messages.getLast();
        String text = chatMessage.getContent().toString();
        log.info("开始知识问答！knowledgeName={}text={}",knowledge.getName(),text);

        try {
            ResultVO<SearchKnowledgeResult> searchResult = embeddingSearchService.searchKnowledgeContent(knowledgeId, text,3,knowledge.getMinScore()==null?0.6:knowledge.getMinScore());
            if(!searchResult.isOk() || CollectionUtils.isEmpty(searchResult.getData().getContents())){
                if(knowledge.getStrict()==1){
                    return ResultVO.fail("不好意思,已经超出我的知识范围，暂时不能回答您,十分抱歉！");
                }
                // 未命中 但是不严格
                return ResultVO.success();
            }

            List<String> dataList = searchResult.getData().getContents();
            StringBuilder newMessage = new StringBuilder("根据已知内容以专业、通俗易懂的方式来回答问题\n");
            for (int i = 0; i < dataList.size(); i++) {
                newMessage.append("已知内容").append(i + 1).append(": ").append(dataList.get(i)).append("\n");
            }
            newMessage.append("问题: ").append(chatMessage.getContent().toString());
            chatMessage.setContent(newMessage.toString());

            response.getTokenUsages().add(searchResult.getData().getTokenUsage());
            response.setDocsSources(searchResult.getData().getSource());
        }catch (Exception e){
            log.error("KnowledgeProcessor error text={} collectionName={}",text,collectionName,e);
        }
        return ResultVO.success();
    }




}
