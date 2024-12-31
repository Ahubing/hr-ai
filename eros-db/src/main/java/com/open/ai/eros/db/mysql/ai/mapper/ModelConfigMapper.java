package com.open.ai.eros.db.mysql.ai.mapper;

import com.open.ai.eros.db.mysql.ai.entity.ModelConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigSearchVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-04
 */

@Mapper
public interface ModelConfigMapper extends BaseMapper<ModelConfig> {


    @Update("update model_config set used_balance = used_balance + #{cost} where id = #{id} ")
    int updateUsedBalance(@Param("id") Long id,@Param("cost") Long cost);



    @Select({
            "<script> ",
            "select count(*) from model_config where 1 = 1   " +
                    "  <if test=\"template != null and template != ''\"> ",
            "    and template = #{template}  ",
            "  </if>" +
                    "  <if test=\"token != null and token != ''\"> ",
            "    and token = #{token}  ",
            "  </if>" +
                    "  <if test=\"status != null  \"> ",
            "    and status = #{status}  ",
            "  </if>" +
                    "  <if test=\"modelConfigName != null  \"> ",
            "    and name  like concat('%',#{modelConfigName},'%')  ",
            "  </if>" +
                    "  <if test=\"id != null  \"> ",
            "    and id = #{id}  ",
            "  </if>" +
                    "</script> "
    })
    int searchModelConfigCount(ModelConfigSearchVo searchVo);


    @Select({
            "<script> ",
            "select * from model_config where 1 = 1   " +
                    "  <if test=\"template != null and template != ''\"> ",
            "    and template = #{template}  ",
            "  </if>" +
                    "  <if test=\" token != null and token != ''\"> ",
            "    and token = #{token}  ",
            "  </if>" +
                    "  <if test=\"status != null  \"> ",
            "    and status = #{status}  ",
            "  </if>" +
                    "  <if test=\"modelConfigName != null  \"> ",
            "    and name like concat('%',#{modelConfigName},'%')  ",
            "  </if>" +
                    "  <if test=\"id != null  \"> ",
            "    and id = #{id}  ",
            "  </if>" +
                    " order by create_time desc limit #{pageIndex},#{pageSize}  ",
            "</script> "
    })
    List<ModelConfig> searchModelConfig(ModelConfigSearchVo searchVo);


}
