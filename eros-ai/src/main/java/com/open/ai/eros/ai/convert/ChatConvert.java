package com.open.ai.eros.ai.convert;


import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.vo.AIParamVo;
import com.open.ai.eros.ai.bean.vo.ChatConversationVo;
import com.open.ai.eros.ai.bean.vo.ChatMessageVo;
import com.open.ai.eros.db.mysql.ai.entity.ChatConversation;
import com.open.ai.eros.db.mysql.ai.entity.ChatMessage;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper
public interface ChatConvert {

    ChatConvert I = Mappers.getMapper(ChatConvert.class);

    ChatMessageVo convertChatMessageVo(ChatMessage chatMessage);


    @Mapping(target = "aiParamVo",source = "aiParam",qualifiedByName="convertAIParam")
    @Mapping(target = "createTime",source = "createTime",qualifiedByName="getTime")
    ChatConversationVo convertChatConversationVo(ChatConversation chatConversation);


    @Named("convertAIParam")
    default AIParamVo convertAIParam(String aiParam){
        if(StringUtils.isNoneEmpty(aiParam)){
            return JSONObject.parseObject(aiParam,AIParamVo.class);
        }
        return null;
    }





    @Named("getTime")
    default String getTime(LocalDateTime createTime){
        LocalDate today = LocalDate.now();
        if (createTime.toLocalDate().isEqual(today)) {
            // 如果是今天，显示小时和分钟
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            return createTime.format(timeFormatter);
        } else {
            // 如果不是今天，显示年-月-日
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return createTime.format(dateFormatter);
        }
    }

}
