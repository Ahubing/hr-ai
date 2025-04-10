package com.open.ai.eros.db.constants;

/**
 * @类名：KnowledgeConstant
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/13 1:12
 */
public interface KnowledgeConstant {


    /**
     * 知识库标识
     */
    String knowledgeName = "knowledge%s";


    /**
     * 文档类向量化队列
     */
    String docsEmbeddingSet = "docsEmbeddingSet";


    /**
     * 内容推理切片队列
     */
    String contentInferQuestionSliceSet = "contentInferQuestionSet";


    /**
     * 标题推理切片队列
     */
    String tileInferSliceSet = "tileInferSliceSet";


}
