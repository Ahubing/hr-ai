package com.open.hr.ai.convert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.vo.AmResumeVo;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmResumeConvert {

    AmResumeConvert I = Mappers.getMapper(AmResumeConvert.class);


    @Mapping(target = "education",source = "education",qualifiedByName="convertToJsonArray")
    @Mapping(target = "experiences",source = "experiences",qualifiedByName="convertToJsonArray")
    @Mapping(target = "projects",source = "projects",qualifiedByName="convertToJsonArray")
    @Mapping(target = "zpData",source = "zpData",qualifiedByName="convertToJsonObject")
    @Mapping(target = "competencyModel",source = "competencyModel",qualifiedByName="convertToJsonObject")
    AmResumeVo convertAmResumeVo(AmResume amResume);

    @Named("convertToJsonObject")
    default JSONObject convertToJsonObject(String jsonString){
        if(StringUtils.isBlank(jsonString)){
            return null;
        }
        return JSONObject.parseObject(jsonString);
    }

    @Named("convertToJsonArray")
    default JSONArray convertToJsonArray(String jsonString){
        if(StringUtils.isBlank(jsonString)){
            return null;
        }
        return JSONArray.parseArray(jsonString);
    }


}
