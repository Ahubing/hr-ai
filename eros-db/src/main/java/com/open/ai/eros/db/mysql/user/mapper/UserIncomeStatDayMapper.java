package com.open.ai.eros.db.mysql.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.ai.eros.db.mysql.user.entity.UserIncomeStatDay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户收益日统计表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Mapper
public interface UserIncomeStatDayMapper extends BaseMapper<UserIncomeStatDay> {


    @Select(" select * from user_income_stat_day order by stat_day desc limit 1 ")
    UserIncomeStatDay getLastStatDay();

}
