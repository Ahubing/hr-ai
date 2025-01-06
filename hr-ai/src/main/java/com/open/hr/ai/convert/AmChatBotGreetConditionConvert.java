package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotGreetCondition;
import com.open.hr.ai.bean.req.AddOrUpdateChatbotGreetCondition;
import com.open.hr.ai.bean.vo.AmChatbotGreetConditionVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmChatBotGreetConditionConvert {

    AmChatBotGreetConditionConvert I = Mappers.getMapper(AmChatBotGreetConditionConvert.class);


    AmChatbotGreetCondition convertAddOrUpdateGreetCondition(AddOrUpdateChatbotGreetCondition addOrUpdateChatbotGreetCondition);

    AmChatbotGreetConditionVo convertGreetConditionVo(AmChatbotGreetCondition amChatbotGreetCondition);



}
