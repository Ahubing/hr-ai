package com.open.ai.eros.text.match.convert;

import com.open.ai.eros.db.mysql.text.entity.AiReplyTemplate;
import com.open.ai.eros.text.match.bean.AiReplyTemplateVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AiReplyTemplateConvert {

    AiReplyTemplateConvert I = Mappers.getMapper(AiReplyTemplateConvert.class);



    AiReplyTemplateVo convertAiReplyTemplateVo(AiReplyTemplate aiReplyTemplate);


}
