package com.open.ai.eros.knowledge.service;

import com.open.ai.eros.ai.lang.chain.provider.EmbeddingProvider;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreApi;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreFactory;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsIn;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DemoVectorStoreService {
    private final EmbeddingProvider provider;

    public DemoVectorStoreService(EmbeddingProvider provider) {
        this.provider = provider;
    }

    /**
     * 仅存储了向量，没有原文本和元数据信息
     *
     * @param text 要嵌入的文本
     * @return 生成ID
     */
    public String embedding(String templateModel,String text) {
        EmbeddingModel embedProvider= provider.embed(templateModel);
        Response<Embedding> embed = embedProvider.embed(text);
        VectorStoreApi embeddingStore = VectorStoreFactory.getVectorStoreApi();

        return embeddingStore.add(embed.content());
    }

    /**
     * 带有元数据的方式
     *
     * @param text 要嵌入的文本
     * @return 生成ID
     */
    public String embeddingWithMeta(String templateModel,String text) {
        EmbeddingModel embedProvider= provider.embed(templateModel);
        TextSegment textSegment = TextSegment.from(text, Metadata.from("userId", "1"));
        Response<Embedding> embed = embedProvider.embed(textSegment);

        VectorStoreApi embeddingStore = VectorStoreFactory.getVectorStoreApi();
        return embeddingStore.add(embed.content(), textSegment);
    }


    public List<String> search(String templateModel,String query) {
        EmbeddingModel embedProvider= provider.embed(templateModel);

        Embedding queryEmbedding = embedProvider.embed(query).content();

        new IsIn("userId", Arrays.asList("1", "2","3"));
        // Filter filter = MetadataFilterBuilder.metadataKey("userId").isEqualTo("1");
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding) // 相似性搜索文本
                .maxResults(1)
                .filter(new IsEqualTo("userId", "1"))
                .minScore(0.6)
                .build();

        VectorStoreApi embeddingStore = VectorStoreFactory.getVectorStoreApi();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);
        return result.matches().stream().map(r-> r.embedded().text()).collect(Collectors.toList());
    }
}

