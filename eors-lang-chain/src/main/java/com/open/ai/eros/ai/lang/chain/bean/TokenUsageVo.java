package com.open.ai.eros.ai.lang.chain.bean;

import lombok.Data;

/**
 * @类名：TokenUsageVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/25 9:23
 */
@Data
public class TokenUsageVo{

    private String model;
    private Integer inputTokenCount;
    private Integer outputTokenCount;
    private Integer totalTokenCount;
}
