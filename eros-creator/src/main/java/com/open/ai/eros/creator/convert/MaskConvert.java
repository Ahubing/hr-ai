package com.open.ai.eros.creator.convert;


import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.creator.bean.req.MaskAddReq;
import com.open.ai.eros.creator.bean.req.MaskUpdateReq;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import com.open.ai.eros.creator.bean.vo.CMaskVo;
import com.open.ai.eros.creator.bean.vo.MaskAIParamVo;
import com.open.ai.eros.creator.bean.vo.loraVo;
import com.open.ai.eros.db.mysql.creator.entity.Mask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mapper
public interface MaskConvert {


    MaskConvert I = Mappers.getMapper(MaskConvert.class);


    @Mapping(target = "tool",source = "tool",qualifiedByName="strConvertTool")
    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="strConvertTemplateModel")
    @Mapping(target = "tags",source = "tags",qualifiedByName="stringToList")
    @Mapping(target = "maskIds",source = "maskIds",qualifiedByName="stringToList")
    @Mapping(target = "greeting",source = "greeting",qualifiedByName="stringToList")
    @Mapping(target = "questionList",source = "questionList",qualifiedByName="stringToList")
    @Mapping(target = "tips",source = "tips",qualifiedByName="stringToList")
    CMaskVo convertCMaskVo(Mask mask);


    @Mapping(target = "tool",source = "tool",qualifiedByName="strConvertTool")
    @Mapping(target = "lora",source = "lora",qualifiedByName="convertLoraVo")
    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="strConvertTemplateModel")
    @Mapping(target = "tags",source = "tags",qualifiedByName="stringToList")
    @Mapping(target = "maskIds",source = "maskIds",qualifiedByName="stringToList")
    @Mapping(target = "greeting",source = "greeting",qualifiedByName="stringToList")
    @Mapping(target = "questionList",source = "questionList",qualifiedByName="stringToList")
    @Mapping(target = "bannedWords",source = "bannedWords",qualifiedByName="stringToList")
    @Mapping(target = "tips",source = "tips",qualifiedByName="stringToList")
    @Mapping(target = "aiParam",source = "aiParam",qualifiedByName="convertMaskAIParamVo")
    BMaskVo convertBMaskVo(Mask mask);


    @Mapping(target = "tool",source = "tool",qualifiedByName="convertToolStr")
    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="convertTemplateModelStr")
    @Mapping(target = "tags",source = "tags",qualifiedByName="listToString")
    @Mapping(target = "maskIds",source = "maskIds",qualifiedByName="listToString")
    @Mapping(target = "greeting",source = "greeting",qualifiedByName="listToString")
    @Mapping(target = "questionList",source = "questionList",qualifiedByName="listToString")
    @Mapping(target = "bannedWords",source = "bannedWords",qualifiedByName="listToString")
    @Mapping(target = "tips",source = "tips",qualifiedByName="listToString")
    @Mapping(target = "aiParam",source = "aiParam",qualifiedByName="convertMaskAIParamVo")
    @Mapping(target = "lora",source = "lora",qualifiedByName="convertLoraVoStr")
    Mask convertMask(MaskAddReq  req);


    @Mapping(target = "tool",source = "tool",qualifiedByName="convertToolStr")
    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="convertTemplateModelStr")
    @Mapping(target = "tags",source = "tags",qualifiedByName="listToString")
    @Mapping(target = "maskIds",source = "maskIds",qualifiedByName="listToString")
    @Mapping(target = "greeting",source = "greeting",qualifiedByName="listToString")
    @Mapping(target = "questionList",source = "questionList",qualifiedByName="listToString")
    @Mapping(target = "bannedWords",source = "bannedWords",qualifiedByName="listToString")
    @Mapping(target = "tips",source = "tips",qualifiedByName="listToString")
    @Mapping(target = "aiParam",source = "aiParam",qualifiedByName="convertMaskAIParamVo")
    @Mapping(target = "lora",source = "lora",qualifiedByName="convertLoraVoStr")
    Mask convertMask(MaskUpdateReq req);




    @Named("convertLoraVoStr")
    default String convertLoraVoStr(List<loraVo> lora){
        if(lora==null){
            return "";
        }
        return JSONObject.toJSONString(lora);
    }


    @Named("convertLoraVo")
    default List<loraVo> convertLoraVo(String lora){
        if(StringUtils.isEmpty(lora)){
            return null;
        }
        return JSONObject.parseArray(lora,loraVo.class);
    }


    @Named("convertTemplateModelStr")
    default String convertTemplateModelStr(List<String> templateModel){
        if(CollectionUtils.isEmpty(templateModel)){
            return "";
        }
        return String.join(",",templateModel);
    }


    @Named("strConvertTemplateModel")
    default List<String> strConvertTemplateModel(String templateModel){
        if(StringUtils.isEmpty(templateModel)){
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(templateModel.split(","));
    }



    @Named("convertToolStr")
    default String convertToolStr(List<String> tool){
        if(CollectionUtils.isEmpty(tool)){
            return "";
        }
        return String.join(",",tool);
    }


    @Named("strConvertTool")
    default List<String> strConvertTool(String tool){
        if(StringUtils.isEmpty(tool)){
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(tool.split(","));
    }




    @Named("convertMaskAIParamVo")
    default String convertMaskAIParamVo(MaskAIParamVo aiParam){
        if(aiParam==null){
            return null;
        }
        return JSONObject.toJSONString(aiParam);
    }




    @Named("convertMaskAIParamVo")
    default MaskAIParamVo convertMaskAIParamVo(String aiParam){
        if(StringUtils.isEmpty(aiParam)){
            return null;
        }
        return JSONObject.parseObject(aiParam,MaskAIParamVo.class);
    }




    @Named("stringToList")
    default List<String> stringToList(String str){
        if(StringUtils.isEmpty(str)){
            return Collections.EMPTY_LIST;
        }
        String[] strings = str.split(",");
        return Arrays.asList(strings);
    }




    @Named("listToString")
    default String listToString(List<String> strs){
        if(CollectionUtils.isEmpty(strs)){
            return "";
        }
        return String.join(",",strs);
    }



}
