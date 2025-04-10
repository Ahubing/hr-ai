package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotOptions;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotOptionsItems;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotPositionOption;
import com.open.hr.ai.bean.vo.AmChatbotOptionsItemsVo;
import com.open.hr.ai.bean.vo.AmChatbotOptionsVo;
import com.open.hr.ai.bean.vo.AmChatbotPositionOptionVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmChatBotPositionItemsConvert {

    AmChatBotPositionItemsConvert I = Mappers.getMapper(AmChatBotPositionItemsConvert.class);


    AmChatbotOptionsVo convertPositionOptionVo(AmChatbotOptions amChatbotOptions);
    AmChatbotOptionsItemsVo convertPositionOptionVoItems(AmChatbotOptionsItems amChatbotOptionsItems);




}
