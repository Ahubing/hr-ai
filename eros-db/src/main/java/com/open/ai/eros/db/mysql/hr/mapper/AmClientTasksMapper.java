package com.open.ai.eros.db.mysql.hr.mapper;

import com.open.ai.eros.db.mysql.hr.entity.AmClientTasks;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * <p>
 * 客户端获取的任务列表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
public interface AmClientTasksMapper extends BaseMapper<AmClientTasks> {


    @Select("SELECT " +
            "SUM(CASE WHEN task_type = 'greet' AND (status = 0 OR status = 1) AND retry_times < 3 THEN 1 ELSE 0 END) AS remaining_greet_tasks, " +
            "SUM(CASE WHEN task_type = 'send_message' AND  sub_type = 'rechat' and (status = 0 OR status = 1)  AND retry_times < 3  THEN 1 ELSE 0 END) AS remaining_rechat_tasks, " +
            "SUM(CASE WHEN task_type = 'send_message' AND  sub_type  = 'send_message' and (status = 0 OR status = 1) AND retry_times < 3   THEN 1 ELSE 0 END) AS remaining_reply_message_tasks, " +
            "SUM(CASE WHEN task_type NOT IN ('greet', 'send_message') AND (status = 0 OR status = 1) AND retry_times < 3  THEN 1 ELSE 0 END) AS remaining_other_tasks " +
            "FROM am_client_tasks " +
            "WHERE boss_id = #{bossId}")
    Map<String, Integer> getTaskStatistics(@Param("bossId") String bossId);

}
