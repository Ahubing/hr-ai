package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import com.open.hr.ai.bean.vo.AmZpLocalAccoutsVo;
import org.mapstruct.factory.Mappers;

/**
 * @Author liuzilin
 * @Date 2025/1/4 13:55
 */
public interface PlatFormsConvert {

    PlatFormsConvert I = Mappers.getMapper(PlatFormsConvert.class);

    AmZpLocalAccoutsVo convertAmZpLocalAccounts(AmZpPlatforms amZpPlatforms);
}
