package com.open.hr.ai.bean.vo;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * <p>
 * 打招呼条件
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
public class AmGreetConditionStaticVo {

    /**
     * 薪资待遇
     */
    private JSONArray AmGreetSalaryVo;
    /**
     * 经验要求
     */
    private JSONArray AmGreetExperienceVo;
    /**
     * 学历要求
     */
    private JSONArray AmGreetEducationVo;
    /**
     * 求职意向
     */
    private JSONArray AmGreetIntentionVo;

}
