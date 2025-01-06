package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotPositionOption;
import com.open.hr.ai.bean.vo.AmChatbotPositionOptionVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmChatBotPositionOptionConvert {

    AmChatBotPositionOptionConvert I = Mappers.getMapper(AmChatBotPositionOptionConvert.class);


    AmChatbotPositionOptionVo convertPositionOptionVo(AmChatbotPositionOption amChatbotPositionOption);



}
