package com.open.ai.eros.knowledge.job;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreApi;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreFactory;
import com.open.ai.eros.ai.manager.ThreadPoolManager;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.util.CountDownLatchWrapper;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.entity.KnowledgeDocs;
import com.open.ai.eros.db.mysql.knowledge.service.impl.DocsSliceServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeDocsServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeServiceImpl;
import com.open.ai.eros.knowledge.process.embedding.DocsEmbeddingProcess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@EnableScheduling
public class DocsEmbeddingJob {

    private static final int threadNum = 5;

    private static ThreadFactory docsEmbeddingThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("docs-embedding-%d").build();
    public static ThreadPoolExecutor docsEmbeddingPool = new ThreadPoolExecutor(threadNum, threadNum, 10, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10000), docsEmbeddingThreadFactory, ThreadPoolManager.getCallerRunsPolicyExecutionHandler());



    @Autowired
    private RedisClient redisClient;

    @Autowired
    private KnowledgeDocsServiceImpl knowledgeDocsService;

    @Autowired
    private KnowledgeServiceImpl knowledgeService;

    @Autowired
    private DocsSliceServiceImpl docsSliceService;

    @Autowired
    private List<DocsEmbeddingProcess> docsEmbeddingProcess;


    @Scheduled(fixedDelay = 2000  )
    public void docsEmbedding(){

        Set<String> spops = redisClient.spop(KnowledgeConstant.docsEmbeddingSet,threadNum);
        if(CollectionUtils.isEmpty(spops)){
            return;
        }
        CountDownLatchWrapper countDownLatchWrapper = new CountDownLatchWrapper(docsEmbeddingPool, 3 * 1000000, spops.size());
        for (String spop : spops) {
            countDownLatchWrapper.submit(() -> {
                try {
                    Long docsId = Long.parseLong(spop);
                    KnowledgeDocs knowledgeDocs = knowledgeDocsService.getById(docsId);
                    if(knowledgeDocs==null){
                        log.info("docsEmbedding knowledgeDocs si null id={}",docsId);
                        return;
                    }
                    startEmbedding(knowledgeDocs);
                } catch (Exception e) {
                    log.error("sliceInterQuestion spop={}",spop,e);
                }
            });
        }
        countDownLatchWrapper.await();
    }

    public void startEmbedding(KnowledgeDocs knowledgeDocs){
        Long docsId = knowledgeDocs.getId();
        Long knowledgeId = knowledgeDocs.getKnowledgeId();
        Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
        if(knowledge==null){
            return;
        }
            try {
                log.info(">>>>>>>>>>>>>> Docs文档向量解析开始，KnowledgeId={}, DocsName={}", knowledgeId, knowledgeDocs.getName());
                String collectName = String.format(KnowledgeConstant.knowledgeName, knowledgeId);
                VectorStoreApi embeddingStore = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase());
                VectorStoreApi vectorStoreApi = embeddingStore.getKnowledgeVectorStore(collectName,knowledge.getDimension());

                List<String> vectorIds = docsSliceService.getDocsVectorIds(docsId);
                if(CollectionUtils.isNotEmpty(vectorIds)){
                    vectorStoreApi.removeAll(vectorIds);
                    docsSliceService.clearDocSlices(docsId);
                }

                for (DocsEmbeddingProcess embeddingProcess : docsEmbeddingProcess) {
                    boolean match = embeddingProcess.match(knowledgeDocs.getType());
                    if(match){
                        embeddingProcess.embedding(knowledge,knowledgeDocs,null);
                    }
                }
            }catch (Exception e){
                log.error(">>>>>>>>>>>>>> Docs文档向量解析异常，KnowledgeId={}, DocsName={} ", knowledgeId, knowledgeDocs.getName(),e);
            }
    }




}
