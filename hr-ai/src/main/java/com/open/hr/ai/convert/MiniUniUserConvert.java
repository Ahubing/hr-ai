package com.open.hr.ai.convert;

import com.open.ai.eros.db.mysql.hr.entity.AmSquareRoles;
import com.open.ai.eros.db.mysql.hr.entity.MiniUniUser;
import com.open.hr.ai.bean.vo.AmSquareRolesVo;
import com.open.hr.ai.bean.vo.MiniUniUserVo;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Date 2025/1/4 13:55
 */
@Mapper
public interface MiniUniUserConvert {

    MiniUniUserConvert I = Mappers.getMapper(MiniUniUserConvert.class);

    MiniUniUserVo converAmMiniUniUserVo(MiniUniUser miniUniUser);


}
