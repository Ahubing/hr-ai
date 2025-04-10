package com.open.ai.eros.pay.goods.bean.vo;

import lombok.Data;

import java.util.List;

/**
 *
 *
 *
 * 权益规则
 *
 * @类名：RightsRuleVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/20 23:36
 */

/**
 * TIME_NUMBER:[
 *         // 按天更新的规则结构
 *     {
 *         start_time: xxx,
 *         endTime:xxxx,
 *         used: 99,
 *         total:100,
 *         every_update_number: 100,
 *         rule: [every_init_used,every_add_total]
 *     },
 *     // 次数
 *     {
 *         start_time: xxx,
 *         endTime:xxxx,
 *         used: 99,
 *         total:100,
 *         every_update_number: null,
 *         rule: null
 *     }
 * ]
 */
@Data
public class RightsRuleVo {

    /**
     * 更新规则
     */
    private List<String> rule;

    /**
     * 每次更新的值
     */
    private Long everyUpdateNumber;


}
