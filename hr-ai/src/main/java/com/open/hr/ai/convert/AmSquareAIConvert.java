package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmPositionSection;
import com.open.ai.eros.db.mysql.hr.entity.AmSquareRoles;
import com.open.hr.ai.bean.vo.AmPositionSectionVo;
import com.open.hr.ai.bean.vo.AmSquareRolesVo;
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
public interface AmSquareAIConvert {

    AmSquareAIConvert I = Mappers.getMapper(AmSquareAIConvert.class);


    @Mapping(target = "keywords",source = "keywords",qualifiedByName="strKeyWords")
    AmSquareRolesVo converAmSquareRolesVo(AmSquareRoles amSquareRoles);




    @Named("strKeyWords")
    default List<String> strKeyWords(String keyWords){
        if(StringUtils.isNotBlank(keyWords)){
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(keyWords.split(","));
    }


}
