package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotGreetTask;
import com.open.hr.ai.bean.req.AddOrUpdateAmChatbotGreetTask;
import com.open.hr.ai.bean.vo.AmChatbotGreetTaskVo;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
public interface AmChatBotGreetTaskConvert {

    AmChatBotGreetTaskConvert I = Mappers.getMapper(AmChatBotGreetTaskConvert.class);


    AmChatbotGreetTask convertAddOrUpdateGreetTask(AddOrUpdateAmChatbotGreetTask amChatbotGreetTask);

    AmChatbotGreetTaskVo convertGreetTaskVo(AmChatbotGreetTask amChatbotGreetTask);



}
