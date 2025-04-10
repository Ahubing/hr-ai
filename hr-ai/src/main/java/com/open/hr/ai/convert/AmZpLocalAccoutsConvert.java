package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.hr.ai.bean.vo.AmZpLocalAccoutsVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface AmZpLocalAccoutsConvert {

    AmZpLocalAccoutsConvert I = Mappers.getMapper(AmZpLocalAccoutsConvert.class);


    AmZpLocalAccoutsVo convertAmZpLocalAccounts(AmZpLocalAccouts amZpLocalAccouts);



}
