package com.open.ai.eros.db.mysql.pay.mapper;

import com.open.ai.eros.db.mysql.pay.entity.RightsSnapshot;
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
 * @since 2024-08-20
 */
@Mapper
public interface RightsSnapshotMapper extends BaseMapper<RightsSnapshot> {


    @Select(" select * from rights_snapshot where rights_id = #{rightsId} order by create_time desc limit 1  ")
    RightsSnapshot getLastRightsSnapshot(@Param("rightsId") Long rightsId);

}
