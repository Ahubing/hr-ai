package com.open.ai.eros.db.mysql.pay.mapper;

import com.open.ai.eros.db.mysql.pay.entity.GoodsSnapshot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 商品快照表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-24
 */
@Mapper
public interface GoodsSnapshotMapper extends BaseMapper<GoodsSnapshot> {



    @Select("select * from goods_snapshot where goods_id = #{goodId} order by id desc limit 1  ")
    GoodsSnapshot getNewGoodsSnapshot(@Param("goodId") Long goodId);

}
