package com.open.ai.eros.db.mysql.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.pay.entity.Rights;
import com.open.ai.eros.db.mysql.pay.entity.RightsSimpleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 权益 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Mapper
public interface RightsMapper extends BaseMapper<Rights> {


    @Select(" select * from  rights where status = #{status}  ")
    List<RightsSimpleVo> getRightsSimple(@Param("status") Integer status);


}
