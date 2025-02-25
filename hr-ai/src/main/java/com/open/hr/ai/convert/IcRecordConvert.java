package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmChatMessage;
import com.open.ai.eros.db.mysql.hr.entity.AmMask;
import com.open.ai.eros.db.mysql.hr.entity.IcRecord;
import com.open.hr.ai.bean.vo.AmChatMessageVo;
import com.open.hr.ai.bean.vo.AmMaskVo;
import com.open.hr.ai.bean.vo.IcRecordVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IcRecordConvert {

    IcRecordConvert I = Mappers.getMapper(IcRecordConvert.class);

    IcRecordVo convertIcRecordVo(IcRecord icRecord);

}
