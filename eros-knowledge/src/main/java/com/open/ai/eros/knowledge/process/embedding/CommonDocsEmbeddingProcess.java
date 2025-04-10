package com.open.ai.eros.knowledge.process.embedding;

import com.open.ai.eros.ai.lang.chain.constants.EmbedConst;
import com.open.ai.eros.ai.lang.chain.provider.EmbeddingProvider;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreApi;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreFactory;
import com.open.ai.eros.common.config.CustomIdGenerator;
import com.open.ai.eros.db.constants.DocsTypeEnum;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.constants.SliceEmbeddingStatus;
import com.open.ai.eros.db.mysql.knowledge.entity.DocsSlice;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.entity.KnowledgeDocs;
import com.open.ai.eros.db.mysql.knowledge.service.impl.DocsSliceServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeDocsServiceImpl;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @类名：CommonDocsEmbeddingProcess
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/16 15:37
 */

/**
 * 普通文档向量化
 */
@Slf4j
@Component
public class CommonDocsEmbeddingProcess  implements DocsEmbeddingProcess{


    @Autowired
    private EmbeddingProvider embeddingProvider;


    @Autowired
    private DocsSliceServiceImpl docsSliceService;


    @Autowired
    private KnowledgeDocsServiceImpl knowledgeDocsService;



    @Autowired
    private CustomIdGenerator customIdGenerator;

    /**
     * 普通文章的方式
     * 1. 切割文章
     * 2. 将文档里面内容 向量化 保存到切片表中
     * @param knowledge
     * @param knowledgeDocs
     */
    @Transactional
    @Override
    public void embedding(Knowledge knowledge, KnowledgeDocs knowledgeDocs,String name) {

        Long knowledgeId = knowledgeDocs.getKnowledgeId();

        String collectName = String.format(KnowledgeConstant.knowledgeName, knowledgeId);
        VectorStoreApi embeddingStore = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase());
        VectorStoreApi vectorStoreApi = embeddingStore.getKnowledgeVectorStore(collectName,knowledge.getDimension());

        // 异步切割 + 向量化
        Document document  = new Document(knowledgeDocs.getContent());
        String templateModel = knowledge.getTemplateModel();
        // 保存切片的原文档数据  1. 切片id
        log.info("开始切割文档 name={}",knowledgeDocs.getName());

        // 切割文档
        DocumentSplitter splitter = EmbeddingProvider.splitter(templateModel,name);
        List<TextSegment> segments = splitter.split(document);
        log.info("切割文档完成 name={} size={}",knowledgeDocs.getName(),segments.size());
        for (TextSegment segment : segments) {
            long id = customIdGenerator.nextId();
            segment.metadata().put(EmbedConst.SPLICE_ID,id);
        }
        List<DocsSlice> docsSlices = new ArrayList<>();
        EmbeddingModel embed = embeddingProvider.embed(templateModel);
        for (TextSegment segment : segments) {
            try {
                Embedding embedding = embed.embed(segment).content();
                String vectorId = vectorStoreApi.add(embedding, segment);

                DocsSlice docsSlice = new DocsSlice();
                Long id = segment.metadata().getLong(EmbedConst.SPLICE_ID);
                docsSlice.setId(id);
                String text = segment.text();
                docsSlice.setKnowledgeId(knowledgeId);
                docsSlice.setDocsId(knowledgeDocs.getId());
                docsSlice.setType(knowledgeDocs.getType());
                docsSlice.setName(knowledgeDocs.getName());
                docsSlice.setUserId(knowledgeDocs.getUserId());
                docsSlice.setCreateTime(LocalDateTime.now());
                docsSlice.setStatus(SliceEmbeddingStatus.EMBEDDING_ED.getStatus());
                docsSlice.setContent(text);
                docsSlice.setVectorId(vectorId);
                docsSlice.setWordNum(text.length());
                docsSlices.add(docsSlice);
                if(docsSlices.size()==10){
                    boolean saveBatch = docsSliceService.saveBatch(docsSlices);
                    log.info("Docs文档向量 保存数量 KnowledgeId={}, DocsName={} size={} saveBatch={}", knowledgeId, knowledgeDocs.getName(),docsSlices.size(),saveBatch);
                    docsSlices = new ArrayList<>();
                }
            }catch (Exception e){
                log.error("common embedding error",e);
            }
        }
        if(CollectionUtils.isNotEmpty(docsSlices)){
            boolean saveBatch = docsSliceService.saveBatch(docsSlices);
            log.info("Docs文档向量 保存数量 KnowledgeId={}, DocsName={} size={} saveBatch={}", knowledgeId, knowledgeDocs.getName(),docsSlices.size(),saveBatch);
        }
        boolean updated = knowledgeDocsService.updateKnowledgeDocsSliceStatusAndNum(knowledgeDocs.getId(), 2, segments.size());
        log.info("Docs文档向量 保存数量 KnowledgeId={}, DocsName={} size={} updated={}", knowledgeId, knowledgeDocs.getName(),docsSlices.size(),updated);
        log.info(">>>>>>>>>>>>>> Docs文档向量解析结束，KnowledgeId={}, DocsName={} ", knowledgeId, knowledgeDocs.getName() );
    }

    @Override
    public boolean match(String type) {
        return DocsTypeEnum.COMMON.name().equals(type);
    }
}
