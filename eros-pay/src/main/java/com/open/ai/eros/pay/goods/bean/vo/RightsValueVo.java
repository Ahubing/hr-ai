package com.open.ai.eros.pay.goods.bean.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @类名：RightsRuleVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/20 23:24
 */

@Data
public class RightsValueVo {


    /**
     * 金额 或者 次数 或者 时间戳 单位 小时
     *
     */
    private Long value;


    /**
     * 下次更新覆盖的值
     */
    private Long updateValue;


    /**
     * 生效开始时间
     */
    private LocalDateTime startTime;

    /**
     * 失效时间
     */
    private LocalDateTime endTime;

    /**
     * 更新规则
     * {
     *     "type": "noUpdate", / "updateBalance byTime  "
     *     "time":
     * }
     *
     */
    private String rule;


}
