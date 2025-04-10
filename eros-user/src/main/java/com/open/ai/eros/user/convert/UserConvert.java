package com.open.ai.eros.user.convert;


import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.user.bean.vo.SearchUserResponseVo;
import com.open.ai.eros.user.bean.vo.SimpleUserVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConvert {


    UserConvert I = Mappers.getMapper(UserConvert.class);

    CacheUserInfoVo convertCacheUserInfoVo(User users);

    SimpleUserVo convertSimpleUserVo(User users);

    SearchUserResponseVo convertSearchUserResponseVo(User users);

    CacheUserInfoVo convertCacheUserInfoVo(CacheUserInfoVo cacheUserInfoVo);

}
