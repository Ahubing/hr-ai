package com.open.ai.eros.permission.convert;


import com.open.ai.eros.db.mysql.permission.entity.Permission;
import com.open.ai.eros.permission.bean.vo.PermissionVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PermissionConvert {


    PermissionConvert I = Mappers.getMapper(PermissionConvert.class);

    PermissionVo convertPermission(Permission permission);

}
