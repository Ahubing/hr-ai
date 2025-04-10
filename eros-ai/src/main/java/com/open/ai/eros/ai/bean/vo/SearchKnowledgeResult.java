package com.open.ai.eros.ai.bean.vo;

import com.open.ai.eros.ai.lang.chain.bean.TokenUsageVo;
import lombok.Data;

import java.util.List;

/**
 * @类名：SearchKnowledgeResult
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/28 17:35
 */

@Data
public class SearchKnowledgeResult {


    private TokenUsageVo tokenUsage;

    private List<String> contents;

    private List<DocsSource> source;



}
