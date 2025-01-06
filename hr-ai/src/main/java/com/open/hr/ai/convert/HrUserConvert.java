package com.open.hr.ai.convert;


import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HrUserConvert {

    HrUserConvert I = Mappers.getMapper(HrUserConvert.class);

    CacheUserInfoVo convertCacheUserInfoVo(AmAdmin users);

    CacheUserInfoVo convertCacheUserInfoVo(CacheUserInfoVo cacheUserInfoVo);

}
