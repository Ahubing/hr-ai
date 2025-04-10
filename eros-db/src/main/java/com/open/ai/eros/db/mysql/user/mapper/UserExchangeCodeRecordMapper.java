package com.open.ai.eros.db.mysql.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.user.entity.UserExchangeCodeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户兑换码记录表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-27
 */
@Mapper
public interface UserExchangeCodeRecordMapper extends BaseMapper<UserExchangeCodeRecord> {


    @Select("select * from user_exchange_code_record where user_id = #{userId} and exchange_code_id = #{codeId} limit 1 ")
    UserExchangeCodeRecord getExchangeCodeRecordByUserIdAndCode(@Param("userId") Long userId, @Param("codeId") Long codeId);


}
