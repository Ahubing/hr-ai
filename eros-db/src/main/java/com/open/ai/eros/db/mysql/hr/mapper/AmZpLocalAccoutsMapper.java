package com.open.ai.eros.db.mysql.hr.mapper;

import com.open.ai.eros.db.mysql.ai.entity.ChatConversation;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 招聘本地账户 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
public interface AmZpLocalAccoutsMapper extends BaseMapper<AmZpLocalAccouts> {

    @Select(" select * from am_zp_local_accouts where  admin_id = #{userId} and type = 1 order by id asc")
    List<AmZpLocalAccouts>  getList(@Param("userId")Long userId);

}
