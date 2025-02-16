package com.open.hr.ai.convert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.entity.UploadAmResume;
import com.open.hr.ai.bean.req.UploadAmResumeUpdateReq;
import com.open.hr.ai.bean.vo.AmResumeVo;
import com.open.hr.ai.bean.vo.UploadAmResumeVo;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmUploadResumeConvert {

    AmUploadResumeConvert I = Mappers.getMapper(AmUploadResumeConvert.class);


    @Mapping(target = "education",source = "education",qualifiedByName="convertToJsonArray")
    @Mapping(target = "experiences",source = "experiences",qualifiedByName="convertToJsonArray")
    @Mapping(target = "projects",source = "projects",qualifiedByName="convertToJsonArray")
    UploadAmResumeVo convertAmResumeVo(UploadAmResume amResume);


    @Mapping(target = "education",source = "education",qualifiedByName="convertToJsonString")
    @Mapping(target = "experiences",source = "experiences",qualifiedByName="convertToJsonString")
    @Mapping(target = "projects",source = "projects",qualifiedByName="convertToJsonString")
    UploadAmResume convertAmResume(UploadAmResumeVo amResume);

    @Mapping(target = "education",source = "education",qualifiedByName="convertToJsonString")
    @Mapping(target = "experiences",source = "experiences",qualifiedByName="convertToJsonString")
    @Mapping(target = "projects",source = "projects",qualifiedByName="convertToJsonString")
    AmResume convertUpdateUploadAmResume(UploadAmResumeUpdateReq amResume);



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

   @Named("convertToJsonString")
    default String convertToJsonString(Object object){
        if(Objects.isNull(object)){
            return null;
        }
        return JSONObject.toJSONString(object);
    }


}
