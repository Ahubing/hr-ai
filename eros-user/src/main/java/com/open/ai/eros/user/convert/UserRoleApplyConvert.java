package com.open.ai.eros.user.convert;

import com.open.ai.eros.db.mysql.user.entity.UserRoleApply;
import com.open.ai.eros.user.bean.req.UserApplyCreatorReq;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserRoleApplyConvert {

    UserRoleApplyConvert I = Mappers.getMapper(UserRoleApplyConvert.class);


    UserRoleApply convertUserRoleApply(UserApplyCreatorReq req);
}
