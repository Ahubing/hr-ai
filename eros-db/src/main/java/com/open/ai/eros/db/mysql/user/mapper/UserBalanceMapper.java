package com.open.ai.eros.db.mysql.user.mapper;

import com.open.ai.eros.db.mysql.user.entity.UserBalance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 用户余额表，存储用户的可提取和不可提取余额 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */
@Mapper
public interface UserBalanceMapper extends BaseMapper<UserBalance> {

    /**
     * 获取用户的余额信息
     *
     * @param userId
     * @return
     */
    @Select(" select  * from user_balance where user_id = #{userId} limit 2 ")
    List<UserBalance> getUserBalance(@Param("userId") Long userId);


    /**
     * 获取用户的单个余额账号
     *
     * @param userId
     * @param type
     * @return
     */
    @Select(" select  * from user_balance where user_id = #{userId} and type = #{type} limit 1 ")
    UserBalance getUserBalanceByType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 更新用户余额
     *
     * @param userId
     * @param opBalance
     * @param type
     * @return
     */
    @Update("update user_balance set balance = #{opBalance} where user_id = #{userId} and type = #{type} limit 1 ")
    int updateUserBalance(@Param("userId") Long userId, @Param("opBalance") Long opBalance, @Param("type") String type);




    @Update("update user_balance set balance = balance + #{opBalance} where user_id = #{userId} and type = #{type} limit 1 ")
    int addUserBalance(@Param("userId") Long userId, @Param("opBalance") Long opBalance, @Param("type") String type);


}
