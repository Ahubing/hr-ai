package com.open.ai.eros.db.mysql.hr.mapper;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotGreetTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;

/**
 * <p>
 * 打招呼任务 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
public interface AmChatbotGreetTaskMapper extends BaseMapper<AmChatbotGreetTask> {

    // 根据accountId 和 positionId 删除任务
    @Delete("delete from am_chatbot_greet_task where account_id = #{accountId} and position_id = #{positionId}")
    int deleteByAccountIdAndPositionId(String accountId, Integer positionId);
}
