package com.open.hr.ai.bean.vo;

import com.open.ai.eros.db.mysql.hr.entity.AmChatbotGreetConfig;
import lombok.Data;

import java.util.List;

/**
 * @Author 
 * @Date 2025/1/15 22:21
 */
@Data
public class AmChatBotGreetConfigDataVo {

    private AmChatbotGreetConfig config;

    private List<AmChatbotGreetTaskVo> tasks;

    private Integer today_searched;

    private Integer this_month_searched;

    private String today_tmp_task;

    private String today_scheduled_task;

    private String current_time_task;

    private String rechat_no_reply_amount;

    private String rechat_ask_resume_amount;

    private AccountDataVo accountData;



}
