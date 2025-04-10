package com.open.ai.eros.ai.lang.chain.vector.impl;

import com.open.ai.eros.ai.lang.chain.config.MilvusVectorStoreProperties;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreApi;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreFactory;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.db.constants.VectorStoreEnum;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @类名：MilvusEmbeddingStoreFactory
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/12 20:05
 */
@Order(-1)
@Component
@Slf4j
public class MilvusVectorStoreApi implements VectorStoreApi {

    /**
     * key是 集合名
     * value则是相应的 集合的操作集合
     */
    private final static Map<String, VectorStoreApi> embeddingStoreMap = new ConcurrentHashMap<>();

    private static MilvusVectorStoreProperties properties;

    private  final String defaultCollectionName = "langchain4j_collection";

    @Setter
    private MilvusEmbeddingStore milvusEmbeddingStore;


    public MilvusVectorStoreApi() {
        VectorStoreFactory.setEmbeddingStore(VectorStoreEnum.MILVUS.getVector(),this);
    }

    @Autowired(required = false)
    public MilvusVectorStoreApi(MilvusVectorStoreProperties properties) {
        if(properties==null || StringUtils.isEmpty(properties.getHost())){
            log.error("MilvusVectorStoreProperties initProperties is null");
            return;
        }
        MilvusVectorStoreApi.properties = properties;
        try {
            setProperties(properties);
        }catch (Exception e){

        }
    }

    /**
     * 兼容原始逻辑，会生成一个配置类中的集合
     * 在项目启动的时候执行
     */
    private void setProperties(MilvusVectorStoreProperties properties){
        if(properties.getDimension()==null){
            throw new BizException("向量数据库矢量长度不能为空！");
        }
        String collectionName = Optional.ofNullable(properties.getCollectionName()).orElse(defaultCollectionName);
        createCollectionVectorStore(collectionName,properties.getDimension());
    }


    @Override
    public VectorStoreApi createCollectionVectorStore(String collectionName,Integer dimension){

        VectorStoreApi vectorStoreApi = embeddingStoreMap.get(collectionName);
        if(vectorStoreApi !=null){
            return vectorStoreApi;
        }
        synchronized (MilvusVectorStoreApi.class){

            vectorStoreApi = embeddingStoreMap.get(collectionName);
            if(vectorStoreApi !=null){
                return vectorStoreApi;
            }

            MilvusVectorStoreApi embeddingStoreFactory2 = new MilvusVectorStoreApi();
            String host = (String) Optional.ofNullable(properties.getHost()).orElse("localhost");
            int port = (Integer)Optional.ofNullable(properties.getPort()).orElse(19530);
            ConsistencyLevelEnum consistencyLevel = Optional.ofNullable(properties.getConsistencyLevel()).orElse(MilvusVectorStoreProperties.DEFAULT_CONSISTENCY_LEVEL);
            String username = Optional.ofNullable(properties.getUsername()).orElse(System.getenv("MILVUS_USERNAME"));
            String password = Optional.ofNullable(properties.getPassword()).orElse(System.getenv("MILVUS_PASSWORD"));
            MilvusEmbeddingStore milvusEmbeddingStore = MilvusEmbeddingStore.builder()
                    .host(host)
                    .port(port)
                    .collectionName(collectionName)
                    .dimension(dimension).indexType(properties.getIndexType())
                    .metricType(properties.getMetricType()).uri(properties.getUri())
                    .token(properties.getToken()).username(username).password(password)
                    .consistencyLevel(consistencyLevel)
                    .retrieveEmbeddingsOnSearch(properties.getRetrieveEmbeddingsOnSearch())
                    .autoFlushOnInsert(properties.getAutoFlushOnInsert())
                    .databaseName(properties.getDatabaseName())
                    .build();
            embeddingStoreFactory2.setMilvusEmbeddingStore(milvusEmbeddingStore);
            embeddingStoreMap.put(collectionName,embeddingStoreFactory2);
            return embeddingStoreFactory2;
        }
    }


    /**
     * 获取默认的集合类管理
     *
     * @return
     */
    @Override
    public VectorStoreApi getGoldVectorStore(){

        OpenAiEmbeddingModelName textEmbedding3Small = OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL;

        return createCollectionVectorStore(defaultCollectionName,textEmbedding3Small.dimension());
    }


    @Override
    public VectorStoreApi getKnowledgeVectorStore(String collectionName, Integer dimension){
        if(dimension==null || dimension<=0){
            throw new  BizException("矢量长度不能为null或者小于0");
        }
        return embeddingStoreMap.getOrDefault(collectionName, createCollectionVectorStore(collectionName,dimension));
    }

    @Override
    public void dropCollection(String collectionName) {
        milvusEmbeddingStore.dropCollection(collectionName);
    }

    @Override
    public String add(Embedding embedding) {
        return this.milvusEmbeddingStore.add(embedding);
    }

    @Override
    public void add(String id, Embedding embedding) {
        this.milvusEmbeddingStore.add(id,embedding);
    }

    @Override
    public String add(Embedding embedding, TextSegment textSegment) {
        return this.milvusEmbeddingStore.add(embedding,textSegment);
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        return this.milvusEmbeddingStore.addAll(embeddings);
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> embedded) {
        return this.milvusEmbeddingStore.addAll(embeddings,embedded);
    }

    @Override
    public void remove(String id) {
        VectorStoreApi.super.remove(id);
    }

    @Override
    public void removeAll(Collection<String> ids) {
        this.milvusEmbeddingStore.removeAll(ids);
    }

    @Override
    public void removeAll(Filter filter) {
        this.milvusEmbeddingStore.removeAll(filter);
    }

    @Override
    public void removeAll() {
        this.milvusEmbeddingStore.removeAll();
    }

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
        return this.milvusEmbeddingStore.search(request);
    }
}
