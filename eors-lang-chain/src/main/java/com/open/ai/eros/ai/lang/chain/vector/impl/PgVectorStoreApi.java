package com.open.ai.eros.ai.lang.chain.vector.impl;

import com.open.ai.eros.ai.lang.chain.config.PgVectorStoreProperties;
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
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Order(-1)
//@Configuration
@Slf4j
public class PgVectorStoreApi implements VectorStoreApi {

    /**
     * key是 集合名
     * value则是相应的 集合的操作集合
     */
    private final static Map<String, VectorStoreApi> embeddingStoreMap = new ConcurrentHashMap<>();

    private static volatile PgVectorStoreProperties properties;

    private  final String defaultCollectionName = "langchain_pg_collection";

    @Setter
    private volatile PgVectorEmbeddingStore pgVectorEmbeddingStore;


    public PgVectorStoreApi() {
        VectorStoreFactory.setEmbeddingStore(VectorStoreEnum.PG_VECTOR.getVector(),this);
    }

    @Autowired(required = false)
    public PgVectorStoreApi(PgVectorStoreProperties properties) {
        if(properties==null || StringUtils.isEmpty(properties.getHost())){
            log.error("PgVectorStoreApi initProperties is null");
            return;
        }
        PgVectorStoreApi.properties = properties;
        setProperties(properties);
    }

    /**
     * 兼容原始逻辑，会生成一个配置类中的集合
     * 在项目启动的时候执行
     */
    private void setProperties(PgVectorStoreProperties prop){
        if(properties.getDimension()==null){
            throw new BizException("向量数据库矢量长度不能为空！");
        }
        String collectionName = Optional.ofNullable(prop.getTable()).orElse(defaultCollectionName);
        createCollectionVectorStore(collectionName,properties.getDimension());
    }


    @Override
    public VectorStoreApi createCollectionVectorStore(String collectionName,Integer dimension){

        VectorStoreApi vectorStoreApi = embeddingStoreMap.get(collectionName);
        if(vectorStoreApi !=null){
            return vectorStoreApi;
        }
        synchronized (PgVectorStoreApi.class){

            vectorStoreApi = embeddingStoreMap.get(collectionName);
            if(vectorStoreApi !=null){
                return vectorStoreApi;
            }

            PgVectorStoreApi embeddingStoreFactory2 = new PgVectorStoreApi();

            PgVectorEmbeddingStore embeddingStore = PgVectorEmbeddingStore.builder()
                    .host(properties.getHost())
                    .port(properties.getPort())
                    .database(properties.getDatabase())
                    .dimension(properties.getDimension())
                    .user(properties.getUser())
                    .password(properties.getPassword())
                    .table(collectionName)
                    .useIndex(properties.getUseIndex())
                    .createTable(properties.getCreateTable())
                    .dropTableFirst(properties.getDropTableFirst())
                    .build();

            embeddingStoreFactory2.setPgVectorEmbeddingStore(embeddingStore);
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
        // todo 不给删除
    }

    @Override
    public String add(Embedding embedding) {
        return this.pgVectorEmbeddingStore.add(embedding);
    }

    @Override
    public void add(String id, Embedding embedding) {
        this.pgVectorEmbeddingStore.add(id,embedding);
    }

    @Override
    public String add(Embedding embedding, TextSegment textSegment) {
        return this.pgVectorEmbeddingStore.add(embedding,textSegment);
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        return this.pgVectorEmbeddingStore.addAll(embeddings);
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> embedded) {
        return this.pgVectorEmbeddingStore.addAll(embeddings,embedded);
    }

    @Override
    public void remove(String id) {
        VectorStoreApi.super.remove(id);
    }

    @Override
    public void removeAll(Collection<String> ids) {
        this.pgVectorEmbeddingStore.removeAll(ids);
    }

    @Override
    public void removeAll(Filter filter) {
        this.pgVectorEmbeddingStore.removeAll(filter);
    }

    @Override
    public void removeAll() {
        this.pgVectorEmbeddingStore.removeAll();
    }

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
        return this.pgVectorEmbeddingStore.search(request);
    }
}
