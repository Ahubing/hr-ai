package com.open.ai.eros.ai.model.bean.vo.gemini.response;

import lombok.Data;

import java.util.LinkedList;

/**
 * @类名：GeminiCandidates
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/17 14:59
 */

@Data
public class GeminiCandidates {

    /**
     * 消息
     */
    private LinkedList<CandidatesMessage> candidates;

    /**
     * 结束 最后一个才有
     */
    private UsageMetadata usageMetadata;


}
