package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import com.open.hr.ai.bean.vo.AmAdminVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmAdminConvert {

    AmAdminConvert I = Mappers.getMapper(AmAdminConvert.class);



//
    AmAdminVo convertAmAdminVo(AmAdmin admin);


}
