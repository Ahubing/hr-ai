package com.open.ai.eros.db.mysql.pay.mapper;

import com.open.ai.eros.db.mysql.pay.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-24
 */

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {


    @Update("update goods set seed_num = seed_num + 1 where id = #{id} limit 1 ")
    int updateGoodsSeedNum(@Param("id") Long id);


}
