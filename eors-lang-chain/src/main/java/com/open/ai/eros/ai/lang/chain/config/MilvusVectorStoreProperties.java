package com.open.ai.eros.ai.lang.chain.config;

import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @类名：MilvusEmbeddingStoreProperties
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/12 1:02
 */
@Configuration
@ConfigurationProperties(
        "langchain4j.milvus"
)
public class MilvusVectorStoreProperties {
    static final String PREFIX = "langchain4j.milvus";
    static final String DEFAULT_HOST = "localhost";
    static final int DEFAULT_PORT = 19530;
    static final String DEFAULT_COLLECTION_NAME = "langchain4j_collection";
    public static final ConsistencyLevelEnum DEFAULT_CONSISTENCY_LEVEL;
    private String host;
    private Integer port;
    private String collectionName;
    private Integer dimension;
    private IndexType indexType;
    private MetricType metricType;
    private String uri;
    private String token;
    private String username;
    private String password;
    private ConsistencyLevelEnum consistencyLevel;
    private Boolean retrieveEmbeddingsOnSearch;
    // 是否刷新
    private Boolean autoFlushOnInsert;
    private String databaseName;

    public MilvusVectorStoreProperties() {
    }

    public String getHost() {
        return this.host;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    public Integer getDimension() {
        return this.dimension;
    }

    public IndexType getIndexType() {
        return this.indexType;
    }

    public MetricType getMetricType() {
        return this.metricType;
    }

    public String getUri() {
        return this.uri;
    }

    public String getToken() {
        return this.token;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public ConsistencyLevelEnum getConsistencyLevel() {
        return this.consistencyLevel;
    }

    public Boolean getRetrieveEmbeddingsOnSearch() {
        return this.retrieveEmbeddingsOnSearch;
    }

    public Boolean getAutoFlushOnInsert() {
        return this.autoFlushOnInsert;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public void setDimension(Integer dimension) {
        this.dimension = dimension;
    }

    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConsistencyLevel(ConsistencyLevelEnum consistencyLevel) {
        this.consistencyLevel = consistencyLevel;
    }

    public void setRetrieveEmbeddingsOnSearch(Boolean retrieveEmbeddingsOnSearch) {
        this.retrieveEmbeddingsOnSearch = retrieveEmbeddingsOnSearch;
    }

    public void setAutoFlushOnInsert(Boolean autoFlushOnInsert) {
        this.autoFlushOnInsert = autoFlushOnInsert;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    static {
        DEFAULT_CONSISTENCY_LEVEL = ConsistencyLevelEnum.STRONG;
    }
}
