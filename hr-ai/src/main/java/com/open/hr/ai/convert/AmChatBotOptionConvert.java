package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotOptions;
import com.open.hr.ai.bean.vo.AmChatbotOptionsVo;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
public interface AmChatBotOptionConvert {

    AmChatBotOptionConvert I = Mappers.getMapper(AmChatBotOptionConvert.class);


    AmChatbotOptionsVo convertOptionVo(AmChatbotOptions amChatbotOptions);



}
