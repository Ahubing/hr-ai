package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotGreetConditionNew;
import com.open.hr.ai.bean.req.AddOrUpdateChatbotGreetConditionNew;
import com.open.hr.ai.bean.vo.AmGreetConditionVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmChatBotGreetNewConditionConvert {

    AmChatBotGreetNewConditionConvert I = Mappers.getMapper(AmChatBotGreetNewConditionConvert.class);


    @Mapping(target = "expectPosition",source = "expectPosition",qualifiedByName="AmGreetListToString")
    @Mapping(target = "filterPosition",source = "filterPosition",qualifiedByName="AmGreetListToString")
    @Mapping(target = "experience",source = "experience",qualifiedByName="AmGreetListToString")
    @Mapping(target = "filterExperience",source = "filterExperience",qualifiedByName="AmGreetListToString")
    @Mapping(target = "degree",source = "degree",qualifiedByName="AmGreetIntegerListToString")
    @Mapping(target = "intention",source = "intention",qualifiedByName="AmGreetIntegerListToString")
    @Mapping(target = "skills",source = "skills",qualifiedByName="AmGreetListToString")
    @Mapping(target = "workYears",source = "workYears",qualifiedByName="AmGreetListToString")
    AmChatbotGreetConditionNew convertAddOrUpdateGreetNewCondition(AddOrUpdateChatbotGreetConditionNew addOrUpdateChatbotGreetConditionNew);

    @Mapping(target = "expectPosition",source = "expectPosition",qualifiedByName="AmGreetStringToList")
    @Mapping(target = "filterPosition",source = "filterPosition",qualifiedByName="AmGreetStringToList")
    @Mapping(target = "experience",source = "experience",qualifiedByName="AmGreetStringToList")
    @Mapping(target = "filterExperience",source = "filterExperience",qualifiedByName="AmGreetStringToList")
    @Mapping(target = "degree",source = "degree",qualifiedByName="AmGreetStringToIntegerList")
    @Mapping(target = "intention",source = "intention",qualifiedByName="AmGreetStringToIntegerList")
    @Mapping(target = "skills",source = "skills",qualifiedByName="AmGreetStringToList")
    @Mapping(target = "workYears",source = "workYears",qualifiedByName="AmGreetStringToList")
    AmGreetConditionVo convertGreetConditionVo(AmChatbotGreetConditionNew amChatbotGreetConditionNew);




    @Named("AmGreetListToString")
    default String AmGreetListToString(List<String> strs){
        if(CollectionUtils.isEmpty(strs)){
            return "";
        }
        return String.join(",",strs);
    }


    @Named("AmGreetStringToList")
    default List<String> AmGreetStringToList(String str){
        if(StringUtils.isEmpty(str)){
            return Collections.EMPTY_LIST;
        }
        String[] strings = str.split(",");
        //转化成List<Integer>
        return Arrays.asList(strings);
    }
    @Named("AmGreetStringToIntegerList")
    default List<Integer> AmGreetStringToIntegerList(String str){
        if(StringUtils.isEmpty(str)){
            return Collections.EMPTY_LIST;
        }
        String[] strings = str.split(",");
        //转化成List<Integer>
        return  Arrays.stream(strings).map(Integer::parseInt).collect(Collectors.toList());
    }

    @Named("AmGreetIntegerListToString")
    default String AmGreetIntegerListToString(List<Integer> str){
        if (CollectionUtils.isEmpty(str)){
            return "";
        }
        String collect = str.stream().map(String::valueOf).collect(Collectors.joining(","));
        return  collect;
    }
}
