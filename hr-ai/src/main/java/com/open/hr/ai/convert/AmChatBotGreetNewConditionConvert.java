package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotGreetCondition;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotGreetConditionNew;
import com.open.hr.ai.bean.req.AddOrUpdateChatbotGreetCondition;
import com.open.hr.ai.bean.req.AddOrUpdateChatbotGreetConditionNew;
import com.open.hr.ai.bean.vo.AmChatbotGreetConditionVo;
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

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmChatBotGreetNewConditionConvert {

    AmChatBotGreetNewConditionConvert I = Mappers.getMapper(AmChatBotGreetNewConditionConvert.class);


    @Mapping(target = "expectPosition",source = "expectPosition",qualifiedByName="AmListToString")
    @Mapping(target = "filterPosition",source = "filterPosition",qualifiedByName="AmListToString")
    @Mapping(target = "experience",source = "experience",qualifiedByName="AmListToString")
    @Mapping(target = "filterExperience",source = "filterExperience",qualifiedByName="AmListToString")
    @Mapping(target = "degree",source = "degree",qualifiedByName="AmListToString")
    @Mapping(target = "intention",source = "intention",qualifiedByName="AmListToString")
    @Mapping(target = "skills",source = "skills",qualifiedByName="AmListToString")
    AmChatbotGreetConditionNew convertAddOrUpdateGreetNewCondition(AddOrUpdateChatbotGreetConditionNew addOrUpdateChatbotGreetConditionNew);

    @Mapping(target = "expectPosition",source = "expectPosition",qualifiedByName="AmStringToList")
    @Mapping(target = "filterPosition",source = "filterPosition",qualifiedByName="AmStringToList")
    @Mapping(target = "experience",source = "experience",qualifiedByName="AmStringToList")
    @Mapping(target = "filterExperience",source = "filterExperience",qualifiedByName="AmStringToList")
    @Mapping(target = "degree",source = "degree",qualifiedByName="AmStringToList")
    @Mapping(target = "intention",source = "intention",qualifiedByName="AmStringToList")
    @Mapping(target = "skills",source = "skills",qualifiedByName="AmStringToList")
    AmGreetConditionVo convertGreetConditionVo(AmChatbotGreetConditionNew amChatbotGreetConditionNew);




    @Named("AmListToString")
    default String AmListToString(List<String> strs){
        if(CollectionUtils.isEmpty(strs)){
            return "";
        }
        return String.join(",",strs);
    }


    @Named("AmStringToList")
    default List<String> AmStringToList(String str){
        if(StringUtils.isEmpty(str)){
            return Collections.EMPTY_LIST;
        }
        String[] strings = str.split(",");
        return Arrays.asList(strings);
    }

}
