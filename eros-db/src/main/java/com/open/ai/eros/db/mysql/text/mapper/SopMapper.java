package com.open.ai.eros.db.mysql.text.mapper;

import com.open.ai.eros.db.mysql.text.entity.Sop;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-10-20
 */

@Mapper
public interface SopMapper extends BaseMapper<Sop> {


    @Select("select  * from  sop where code = #{code} limit 1  ")
    Sop getSopBuSceneCode(@Param("code") String code);

}
