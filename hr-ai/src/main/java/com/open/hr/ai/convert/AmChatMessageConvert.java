package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmChatMessage;
import com.open.ai.eros.db.mysql.hr.entity.AmPositionSection;
import com.open.hr.ai.bean.vo.AmChatMessageVo;
import com.open.hr.ai.bean.vo.AmPositionSectionVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmChatMessageConvert {

    AmChatMessageConvert I = Mappers.getMapper(AmChatMessageConvert.class);


    AmChatMessageVo convertAmChatMessageVo(AmChatMessage amChatMessage);



}
