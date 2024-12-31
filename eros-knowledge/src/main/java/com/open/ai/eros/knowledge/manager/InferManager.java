package com.open.ai.eros.knowledge.manager;

import com.open.ai.eros.ai.lang.chain.constants.EmbedConst;
import com.open.ai.eros.ai.lang.chain.provider.EmbeddingProvider;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreApi;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreFactory;
import com.open.ai.eros.ai.vector.process.ContentInferQuestionAIProcess;
import com.open.ai.eros.ai.vector.process.DocsTitleAIProcess;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.constants.SliceEmbeddingStatus;
import com.open.ai.eros.db.mysql.knowledge.entity.DocsSlice;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.service.impl.DocsSliceServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeServiceImpl;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @类名：InferManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/16 21:35
 */
@Component
@Slf4j
public class InferManager {

    @Autowired
    private DocsSliceServiceImpl docsSliceService;

    @Autowired
    private ContentInferQuestionAIProcess contentInferQuestionAIProcess;


    @Autowired
    private DocsTitleAIProcess docsTitleAIProcess;

    @Autowired
    private KnowledgeServiceImpl knowledgeService;


    @Autowired
    private EmbeddingProvider embeddingProvider;


    public void inferSliceQuestion(Long sliceId){
        try {
            log.info("开始推理切片问题-------");
            DocsSlice docsSlice = docsSliceService.getById(sliceId);
            if(docsSlice==null){
                return;
            }

            Long knowledgeId = docsSlice.getKnowledgeId();
            Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
            if(knowledge==null){
                return;
            }
            String collectName = String.format(KnowledgeConstant.knowledgeName, knowledgeId);
            VectorStoreApi embeddingStore = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase());
            VectorStoreApi vectorStoreApi = embeddingStore.getKnowledgeVectorStore(collectName,knowledge.getDimension());

            // 如果已经向量化了，删除久的
            String vectorId = docsSlice.getVectorId();
            if(StringUtils.isNoneEmpty(vectorId)){
                String[] split = vectorId.split(",");
                vectorStoreApi.removeAll(Arrays.asList(split));
            }

            List<String> inferQuestions = contentInferQuestionAIProcess.getInferQuestion(docsSlice.getContent());
            if(CollectionUtils.isEmpty(inferQuestions)){
                return;
            }
            List<TextSegment> segments = new ArrayList<>();
            for (String inferQuestion : inferQuestions) {
                TextSegment textSegment = TextSegment.from(inferQuestion);
                textSegment.metadata().put(EmbedConst.SPLICE_ID, docsSlice.getId());
                segments.add(textSegment);
            }
            EmbeddingModel embed = embeddingProvider.embed(knowledge.getTemplateModel());
            List<Embedding> embeddings = embed.embedAll(segments).content();
            List<String> ids = vectorStoreApi.addAll(embeddings, segments);

            String join = String.join(",", ids);
            DocsSlice newDocsSlice = new DocsSlice();
            newDocsSlice.setId(sliceId);
            newDocsSlice.setVectorId(join);
            newDocsSlice.setStatus(SliceEmbeddingStatus.EMBEDDING_ED.getStatus());
            boolean updated = docsSliceService.updateById(newDocsSlice);
            if(updated){
                log.info("inferSliceQuestion  KnowledgeId={}, DocsName={} size={} updated={}", knowledgeId, docsSlice.getName(),ids.size(),updated);
            }
        }catch (Exception e){
            log.error("inferSliceQuestion error sliceId={}",sliceId,e);
        }finally {
            log.info("推理切片问题结束-------");
        }
    }



    public void inferSliceTitle(Long sliceId){
        try {
            DocsSlice docsSlice = docsSliceService.getById(sliceId);
            if(docsSlice==null){
                return;
            }

            Long knowledgeId = docsSlice.getKnowledgeId();
            Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
            if(knowledge==null){
                return;
            }
            String collectName = String.format(KnowledgeConstant.knowledgeName, knowledgeId);
            VectorStoreApi embeddingStore = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase());
            VectorStoreApi vectorStoreApi = embeddingStore.getKnowledgeVectorStore(collectName,knowledge.getDimension());

            // 如果已经向量化了，删除久的
            String vectorId = docsSlice.getVectorId();
            if(StringUtils.isNoneEmpty(vectorId)){
                String[] split = vectorId.split(",");
                vectorStoreApi.removeAll(Arrays.asList(split));
            }

            List<String> inferQuestions = docsTitleAIProcess.getInferTitle(docsSlice.getName());
            if(CollectionUtils.isEmpty(inferQuestions)){
                return;
            }
            List<TextSegment> segments = new ArrayList<>();
            for (String inferQuestion : inferQuestions) {
                TextSegment textSegment = TextSegment.from(inferQuestion);
                textSegment.metadata().put(EmbedConst.SPLICE_ID, docsSlice.getId());
                segments.add(textSegment);
            }
            EmbeddingModel embed = embeddingProvider.embed(knowledge.getTemplateModel());
            List<Embedding> embeddings = embed.embedAll(segments).content();
            List<String> ids = vectorStoreApi.addAll(embeddings, segments);

            String join = String.join(",", ids);
            DocsSlice newDocsSlice = new DocsSlice();
            newDocsSlice.setId(sliceId);
            newDocsSlice.setVectorId(join);
            newDocsSlice.setStatus(SliceEmbeddingStatus.EMBEDDING_ED.getStatus());
            boolean updated = docsSliceService.updateById(newDocsSlice);
            if(updated){
                log.info("inferSliceQuestion  KnowledgeId={}, DocsName={} size={} updated={}", knowledgeId, docsSlice.getName(),ids.size(),updated);
            }
        }catch (Exception e){
            log.error("inferSliceQuestion error sliceId={}",sliceId,e);
        }
    }




}
