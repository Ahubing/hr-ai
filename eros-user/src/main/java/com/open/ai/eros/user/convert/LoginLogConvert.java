package com.open.ai.eros.user.convert;

import com.open.ai.eros.db.mysql.user.entity.LoginLog;
import com.open.ai.eros.user.bean.vo.LoginLogVo;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapper;

@Mapper
public interface LoginLogConvert {

    LoginLogConvert I = Mappers.getMapper(LoginLogConvert.class);

    /**
     * 将 LoginLog 实体类转换为 LoginLogVO
     *
     * @param loginLog 登录日志实体类
     * @return 登录日志 VO
     */
    LoginLogVo convertLoginLogVO(LoginLog loginLog);

}
