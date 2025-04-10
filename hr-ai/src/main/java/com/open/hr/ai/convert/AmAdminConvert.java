package com.open.hr.ai.convert;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import com.open.hr.ai.bean.vo.AmAdminVo;
import com.open.hr.ai.bean.vo.SlackOffVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmAdminConvert {

    AmAdminConvert I = Mappers.getMapper(AmAdminConvert.class);



//
@Mapping(target = "slackOff",source = "slackOff",qualifiedByName="convertToJsonObject")
AmAdminVo convertAmAdminVo(AmAdmin admin);


    @Named("convertToJsonObject")
    default SlackOffVo AmStrConvertTemplateModel(String slackOffVo){
        return JSONObject.parseObject(slackOffVo,SlackOffVo.class);
    }

}
