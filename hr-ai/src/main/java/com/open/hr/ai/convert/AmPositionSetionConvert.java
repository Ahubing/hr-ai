package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.ai.eros.db.mysql.hr.entity.AmPositionSection;
import com.open.hr.ai.bean.vo.AmPositionSectionVo;
import com.open.hr.ai.bean.vo.AmPositionVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmPositionSetionConvert {

    AmPositionSetionConvert I = Mappers.getMapper(AmPositionSetionConvert.class);


    AmPositionSectionVo converAmPositionSectionVo(AmPositionSection amPositionSection);



}
