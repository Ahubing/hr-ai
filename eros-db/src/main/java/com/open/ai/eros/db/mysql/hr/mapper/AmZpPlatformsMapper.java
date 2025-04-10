package com.open.ai.eros.db.mysql.hr.mapper;

import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 招聘平台
 * Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
public interface AmZpPlatformsMapper extends BaseMapper<AmZpPlatforms> {


    @Delete(" delete from am_zp_platforms where id = #{id} limit 1  ")
    int deletePlatFormById(@Param("id") Long id);

}
