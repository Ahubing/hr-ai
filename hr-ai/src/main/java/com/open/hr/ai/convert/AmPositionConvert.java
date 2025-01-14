package com.open.hr.ai.convert;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotPositionOption;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.hr.ai.bean.vo.AmChatbotPositionOptionVo;
import com.open.hr.ai.bean.vo.AmPositionVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmPositionConvert {

    AmPositionConvert I = Mappers.getMapper(AmPositionConvert.class);


    @Mapping(target = "extendParams",source = "extendParams",qualifiedByName="toJsonObject")
    AmPositionVo converAmPositionVo(AmPosition amPosition);


    @Named("toJsonObject")
    default JSONObject toJsonObject(String extendParams){
       return JSONObject.parseObject(extendParams);
    }

}
