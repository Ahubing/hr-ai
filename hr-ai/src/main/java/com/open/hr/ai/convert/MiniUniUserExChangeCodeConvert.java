package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.MiniUniUser;
import com.open.ai.eros.db.mysql.hr.entity.MiniUniUserExchangeCode;
import com.open.hr.ai.bean.vo.MiniUniUserExchangeCodeVo;
import com.open.hr.ai.bean.vo.MiniUniUserVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface MiniUniUserExChangeCodeConvert {

    MiniUniUserExChangeCodeConvert I = Mappers.getMapper(MiniUniUserExChangeCodeConvert.class);

    MiniUniUserExchangeCodeVo convertExchangeCodeVo(MiniUniUserExchangeCode miniUniUserExchangeCode);


}
