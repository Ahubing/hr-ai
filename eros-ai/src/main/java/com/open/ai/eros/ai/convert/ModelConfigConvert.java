package com.open.ai.eros.ai.convert;

import com.open.ai.eros.ai.bean.req.ModelConfigAddReq;
import com.open.ai.eros.ai.bean.req.ModelConfigUpdateReq;
import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfig;
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


@Mapper
public interface ModelConfigConvert {


    ModelConfigConvert I = Mappers.getMapper(ModelConfigConvert.class);

    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="convertTemplateModelStr")
    ModelConfig convertModelConfig(ModelConfigUpdateReq req);

    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="convertTemplateModelStr")
    ModelConfig convertModelConfig(ModelConfigAddReq req);


    @Mapping(target = "templateModel",source = "templateModel",qualifiedByName="strConvertTemplateModel")
    @Mapping(target = "usedBalanceStr",source = "usedBalance",qualifiedByName="strConvertUsedBalanced")
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

    @Named("strConvertUsedBalanced")
    default String strConvertUsedBalanced(Long usedBalance){
        if(Objects.isNull(usedBalance) || usedBalance == 0){
            return "0$";
        }
        return BalanceFormatUtil.getUserExactBalance(usedBalance)+"$";
    }




}
