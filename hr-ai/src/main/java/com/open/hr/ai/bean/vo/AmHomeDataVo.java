package com.open.hr.ai.bean.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @Author
 * @Date 2025/1/15 19:24
 */
@Data
public class AmHomeDataVo {

    private Integer open_positions;

    private Integer new_resume;

    private Integer pending_interview;

    private Integer total_resume;

    private AmRobotVo ai_robot;

    private AmRobotNewsVo news;

    private JSONObject data_funnels;

    private List<Integer> last_20_day_resumes;
}
