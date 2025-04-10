package com.open.ai.eros.db.mysql.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.user.entity.ExchangeCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 兑换码表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-27
 */
@Mapper
public interface ExchangeCodeMapper extends BaseMapper<ExchangeCode> {


    @Select("select * from exchange_code where code = #{code} limit 1 ")
    ExchangeCode getExchangeCodeByCode(@Param("code") String code);



    @Update("update exchange_code set  used_num  =  used_num + #{num} where id = #{id} and  total > used_num ")
    int updateUsedNum(@Param("id") Long id,@Param("num") Integer num);


    @Select("select  * from  exchange_code where user_id = #{userId} and  status = #{status}    order by  create_time  desc   limit #{pageIndex},#{pageSize} ")
    List<ExchangeCode> getExchangeCode(@Param("userId") Long userId, @Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize, @Param("status") Integer status);

}
