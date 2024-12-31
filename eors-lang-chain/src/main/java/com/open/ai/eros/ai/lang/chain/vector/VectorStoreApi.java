package com.open.ai.eros.ai.lang.chain.vector;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;

public interface VectorStoreApi extends EmbeddingStore<TextSegment> {


    /**
     * 创建集合相关的操作配置（新建集合）
     *
     * @param collectionName
     * @param dimension 矢量长度
     * @return
     */
    VectorStoreApi createCollectionVectorStore(String collectionName,Integer dimension);

    /**
     * 获取集合相关的操作配置
     *
     * @param collectionName
     * @return
     */
    VectorStoreApi getKnowledgeVectorStore(String collectionName, Integer dimension);

    /**
     * 获取 默认集合相关的操作配置
     *
     * @return
     */
    VectorStoreApi getGoldVectorStore();

    /**
     * 删除集合
     * @param collectionName
     */
    void dropCollection(String collectionName);

}
