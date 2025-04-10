package com.open.ai.eros.text.match.model.filterWord.bean.vo;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于保存敏感词在字典树的叶子节点，不能共用的信息的信息
 */
@Data
public class FilterWordNodeInfo {

    /**
     * 敏感词id
     */
    private Long id;

    /**
     * 自动回复的id
     */
    private volatile ConcurrentHashMap<Long,Long> replyMap;

    /**
     * 语言
     */
    private volatile String language;

    /**
     * 风险类型
     */
    private volatile Integer riskType;

    /**
     * 风险等级
     */
    private volatile Integer riskLevel;
}
