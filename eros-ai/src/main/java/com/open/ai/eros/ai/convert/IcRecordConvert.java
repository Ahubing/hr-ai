package com.open.ai.eros.ai.convert;

import com.open.ai.eros.ai.bean.vo.IcRecordVo;
import com.open.ai.eros.db.mysql.hr.entity.IcRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IcRecordConvert {

    IcRecordConvert I = Mappers.getMapper(IcRecordConvert.class);

    IcRecordVo convertIcRecordVo(IcRecord icRecord);

}
