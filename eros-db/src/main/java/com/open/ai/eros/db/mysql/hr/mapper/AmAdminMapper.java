package com.open.ai.eros.db.mysql.hr.mapper;

import com.open.ai.eros.db.mysql.hr.entity.AmAdmin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.user.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
public interface AmAdminMapper extends BaseMapper<AmAdmin> {

    @Select("select  * from am_admin where username = #{account} and `status` = 2  limit 1  ")
    AmAdmin getUserByAccount(@Param("account") String account);
}
