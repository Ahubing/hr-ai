package com.open.ai.eros.knowledge.process.embedding;

import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.db.constants.DocsTypeEnum;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.constants.SliceEmbeddingStatus;
import com.open.ai.eros.db.mysql.knowledge.entity.DocsSlice;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.entity.KnowledgeDocs;
import com.open.ai.eros.db.mysql.knowledge.service.impl.DocsSliceServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeDocsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @类名：CommonDocsEmbeddingProcess
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/16 15:37
 */

/**
 * 方案类文档向量化
 */
@Component
public class PlanDocsEmbeddingProcess   implements DocsEmbeddingProcess{


    @Autowired
    private DocsSliceServiceImpl docsSliceService;

    @Autowired
    private KnowledgeDocsServiceImpl knowledgeDocsService;

    @Autowired
    private RedisClient redisClient;

    /**
     * 标题变种
     *
     * @param knowledge
     * @param knowledgeDocs
     */
    @Transactional
    @Override
    public void embedding(Knowledge knowledge, KnowledgeDocs knowledgeDocs,String name) {
        DocsSlice docsSlice = new DocsSlice();
        docsSlice.setKnowledgeId(knowledgeDocs.getKnowledgeId());
        docsSlice.setDocsId(knowledgeDocs.getId());
        docsSlice.setName(knowledgeDocs.getName());
        docsSlice.setUserId(knowledgeDocs.getUserId());
        docsSlice.setCreateTime(LocalDateTime.now());
        docsSlice.setType(knowledgeDocs.getType());
        docsSlice.setStatus(SliceEmbeddingStatus.EMBEDDING.getStatus());
        docsSlice.setContent(knowledgeDocs.getContent());
        docsSlice.setWordNum(knowledgeDocs.getContent().length());
        boolean saveBatch = docsSliceService.save(docsSlice);
        if(saveBatch){
            knowledgeDocsService.updateKnowledgeDocsSliceStatusAndNum(knowledgeDocs.getId(), 2, 1);
            // 开始 ai变种标题
            startInterQuestion(docsSlice);
        }
    }



    private void startInterQuestion(DocsSlice docsSlice){
        redisClient.sadd(KnowledgeConstant.tileInferSliceSet,docsSlice.getId());
    }


    @Override
    public boolean match(String type) {
        return DocsTypeEnum.PLAN.name().equals(type);
    }
}
