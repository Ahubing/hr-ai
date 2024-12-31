package com.open.ai.eros.ai.model.processor.before;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import com.open.ai.eros.ai.model.processor.AIChatBeforeProcessor;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import com.open.ai.eros.creator.bean.vo.MaskAIParamVo;
import com.open.ai.eros.creator.bean.vo.loraVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @类名：MaskMessageBeforeProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/11/7 9:25
 */
@Order(60)
@Component
@Slf4j
public class MaskMessageBeforeProcessor  implements AIChatBeforeProcessor {


    @Override
    public ResultVO<Void> aiChatBefore(AITextChatVo chatReq, Long userId, SendMessageUtil sendMessageUtil) {

        BMaskVo bMaskVo = chatReq.getBMaskVo();
        if(bMaskVo==null){
            return ResultVO.success();
        }
        MaskAIParamVo aiParam = bMaskVo.getAiParam();
        /**
         * 彩蛋
         */
        List<loraVo> loraList = bMaskVo.getLora();
        if(CollectionUtils.isNotEmpty(loraList)){
            ChatMessage systemMessage = aiParam.getMessages().getFirst();
            // 用户的上下文
            LinkedList<ChatMessage> messages = chatReq.getMessages();
            for (loraVo loraVo : loraList) {
                List<String> keywords = loraVo.getKeyword();
                if(CollectionUtils.isNotEmpty(keywords)){
                    ok:
                    for (String keyword : keywords) {
                        for (ChatMessage message : messages) {
                            String content = message.getContent().toString();
                            if(content.contains(keyword)){
                                // 命中彩蛋
                                String systemContent = systemMessage.getContent().toString();
                                systemContent = systemContent + "\n" + loraVo.getContent();
                                systemMessage.setContent(systemContent);
                                break ok;
                            }
                        }
                    }
                }
            }
        }
        // 世界书

        // 用户的前缀用语
        String format = "%s%s%s";
        ChatMessage last = chatReq.getMessages().getLast();
        last.setContent(
                String.format(format, getStr(bMaskVo.getUserPrefix()), last.getContent().toString(), getStr(bMaskVo.getUserSuffix()))
        );

        return ResultVO.success();
    }


    private String getStr(String prefix){
        if(StringUtils.isEmpty(prefix)){
            return "";
        }
        return prefix;
    }


}
