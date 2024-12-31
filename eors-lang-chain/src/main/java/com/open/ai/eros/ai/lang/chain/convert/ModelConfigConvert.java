package com.open.ai.eros.ai.lang.chain.convert;

import com.open.ai.eros.db.mysql.ai.entity.ModelConfig;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
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
public interface ModelConfigConvert {


    ModelConfigConvert I = Mappers.getMapper(ModelConfigConvert.class);


    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="strConvertTemplateModel")
    ModelConfigVo convertModelConfigVo(ModelConfig config);


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




}
