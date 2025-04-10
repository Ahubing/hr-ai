package com.open.hr.ai.convert;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.hr.entity.AmMask;
import com.open.hr.ai.bean.req.AmMaskAddReq;
import com.open.hr.ai.bean.req.AmMaskUpdateReq;
import com.open.hr.ai.bean.vo.AmMaskAIParamVo;
import com.open.hr.ai.bean.vo.AmMaskVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmMaskConvert {

    AmMaskConvert I = Mappers.getMapper(AmMaskConvert.class);



//
    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="AmStrConvertTemplateModel")
    @Mapping(target = "tags",source = "tags",qualifiedByName="AmStringToList")
    @Mapping(target = "aiParam",source = "aiParam",qualifiedByName="convertAmMaskAIParamVo")
    AmMaskVo convertAmMaskVo(AmMask amMask);


    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="AmConvertTemplateModelStr")
    @Mapping(target = "tags",source = "tags",qualifiedByName="AmListToString")
    @Mapping(target = "aiParam",source = "aiParam",qualifiedByName="convertAmMaskAIParamToJson")
    AmMask convertUpdateAmMaskReq(AmMaskUpdateReq req);




    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="AmConvertTemplateModelStr")
    @Mapping(target = "tags",source = "tags",qualifiedByName="AmListToString")
    @Mapping(target = "aiParam",source = "aiParam",qualifiedByName="convertAmMaskAIParamToJson")
    AmMask convertAddAmMaskReq(AmMaskAddReq req);





    @Named("AmStrConvertTemplateModel")
    default List<String> AmStrConvertTemplateModel(String templateModel){
        if(StringUtils.isEmpty(templateModel)){
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(templateModel.split(","));
    }

    @Named("AmStringToList")
    default List<String> AmStringToList(String str){
        if(StringUtils.isEmpty(str)){
            return Collections.EMPTY_LIST;
        }
        String[] strings = str.split(",");
        return Arrays.asList(strings);
    }

    @Named("convertAmMaskAIParamVo")
    default AmMaskAIParamVo convertAmMaskAIParamVo(String aiParam){
        if(StringUtils.isEmpty(aiParam)){
            return null;
        }
        return JSONObject.parseObject(aiParam,AmMaskAIParamVo.class);
    }

   @Named("convertAmMaskAIParamToJson")
    default String convertAmMaskAIParamToJson(AmMaskAIParamVo aiParam){
        if(Objects.isNull(aiParam)){
            return null;
        }
        return JSONObject.toJSONString(aiParam);
    }



    @Named("AmListToString")
    default String AmListToString(List<String> strs){
        if(CollectionUtils.isEmpty(strs)){
            return "";
        }
        return String.join(",",strs);
    }

    @Named("AmConvertTemplateModelStr")
    default String AmConvertTemplateModelStr(List<String> templateModel){
        if(CollectionUtils.isEmpty(templateModel)){
            return "";
        }
        return String.join(",",templateModel);
    }



}
