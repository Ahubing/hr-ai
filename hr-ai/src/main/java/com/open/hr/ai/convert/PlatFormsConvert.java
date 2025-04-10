package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import com.open.hr.ai.bean.vo.AmZpLocalAccoutsVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface PlatFormsConvert {

    PlatFormsConvert I = Mappers.getMapper(PlatFormsConvert.class);

}
