package com.open.ai.eros.user.convert;

import com.open.ai.eros.db.mysql.user.entity.UserBalanceRecord;
import com.open.ai.eros.user.bean.vo.UserBalanceRecordVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserBalanceRecordConvert {

    UserBalanceRecordConvert I = Mappers.getMapper(UserBalanceRecordConvert.class);

    UserBalanceRecordVo convertToVo(UserBalanceRecord userBalanceRecord);

}
