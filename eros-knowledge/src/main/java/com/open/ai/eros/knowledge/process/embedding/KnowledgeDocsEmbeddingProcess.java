package com.open.ai.eros.knowledge.process.embedding;

import com.open.ai.eros.ai.lang.chain.constants.EmbedConst;
import com.open.ai.eros.ai.lang.chain.provider.EmbeddingProvider;
import com.open.ai.eros.common.config.CustomIdGenerator;
import com.open.ai.eros.common.service.RedisClient;
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
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
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
 * 知识类文档向量化
 */
@Slf4j
@Component
public class KnowledgeDocsEmbeddingProcess   implements DocsEmbeddingProcess{

    @Autowired
    private DocsSliceServiceImpl docsSliceService;

    @Autowired
    private KnowledgeDocsServiceImpl knowledgeDocsService;


    @Autowired
    private CustomIdGenerator customIdGenerator;

    @Autowired
    private RedisClient redisClient;


    /**
     * 将文档 1. 切片 2.使用ai进行推理问题，再向量化推理问题
     *
     * @param knowledge
     * @param knowledgeDocs
     */
    @Transactional
    @Override
    public void embedding(Knowledge knowledge, KnowledgeDocs knowledgeDocs,String name) {
        Long knowledgeId = knowledgeDocs.getKnowledgeId();
        // 异步切割 + 向量化
        Document document  = new Document(knowledgeDocs.getContent());
        String templateModel = knowledge.getTemplateModel();
        // 切割文档
        log.info("开始切割文档 name={}",knowledgeDocs.getName());
        DocumentSplitter splitter = EmbeddingProvider.splitter(templateModel,name);
        List<TextSegment> segments = splitter.split(document);
        log.info("切割文档完成 name={}",knowledgeDocs.getName());

        for (TextSegment segment : segments) {
            long id = customIdGenerator.nextId();
            segment.metadata().put(EmbedConst.SPLICE_ID,id);
        }

        List<DocsSlice> docsSlices = new ArrayList<>();
        for (TextSegment segment : segments) {
            DocsSlice docsSlice = new DocsSlice();
            Long id = segment.metadata().getLong(EmbedConst.SPLICE_ID);
            docsSlice.setId(id);
            String text = segment.text();
            docsSlice.setKnowledgeId(knowledgeId);
            docsSlice.setDocsId(knowledgeDocs.getId());
            docsSlice.setName(knowledgeDocs.getName());
            docsSlice.setType(knowledgeDocs.getType());
            docsSlice.setUserId(knowledgeDocs.getUserId());
            docsSlice.setCreateTime(LocalDateTime.now());
            docsSlice.setStatus(SliceEmbeddingStatus.EMBEDDING.getStatus());
            docsSlice.setContent(text);
            docsSlice.setWordNum(text.length());
            docsSlices.add(docsSlice);
        }
        boolean saveBatch = docsSliceService.saveBatch(docsSlices);
        if(saveBatch){
            knowledgeDocsService.updateKnowledgeDocsSliceStatusAndNum(knowledgeDocs.getId(), 2, segments.size());
            // 开始 ai推理内容
            startInterQuestion(docsSlices);
        }
        log.info(">>>>>>>>>>>>>> Docs文档向量解析结束，KnowledgeId={}, DocsName={} saveBatch={}", knowledgeId, knowledgeDocs.getName(),saveBatch);
    }


    private void startInterQuestion(List<DocsSlice> docsSlices){
        for (DocsSlice docsSlice : docsSlices) {
            redisClient.sadd(KnowledgeConstant.contentInferQuestionSliceSet,String.valueOf(docsSlice.getId()));
        }
    }



    @Override
    public boolean match(String type) {
        return DocsTypeEnum.KNOWLEDGE.name().equals(type);
    }
}
