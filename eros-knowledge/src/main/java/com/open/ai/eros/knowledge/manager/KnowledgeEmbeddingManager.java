package com.open.ai.eros.knowledge.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.ai.lang.chain.constants.EmbedConst;
import com.open.ai.eros.ai.lang.chain.provider.EmbeddingProvider;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreApi;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreFactory;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.service.RedisClient;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.DocsTypeEnum;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.constants.SliceEmbeddingStatus;
import com.open.ai.eros.db.mysql.knowledge.entity.DocsSlice;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.entity.KnowledgeDocs;
import com.open.ai.eros.db.mysql.knowledge.service.impl.DocsSliceServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeDocsServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeServiceImpl;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @类名：KnowledgeEmbeddingManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/12 19:50
 */

@Component
@Slf4j
public class KnowledgeEmbeddingManager {


    @Autowired
    private KnowledgeDocsServiceImpl knowledgeDocsService;


    @Autowired
    private DocsSliceServiceImpl docsSliceService;

    @Autowired
    private KnowledgeServiceImpl knowledgeService;

    @Autowired
    private EmbeddingProvider embeddingProvider;

    @Autowired
    private RedisClient redisClient;




    /**
     * 删除向量
     * @param userId
     * @param sliceId
     * @param role
     * @return
     */
    public ResultVO deleteEmbeddingSlice(Long userId,Long sliceId,String role){
        DocsSlice docsSlice = docsSliceService.getById(sliceId);
        if(docsSlice==null || (!docsSlice.getUserId().equals(userId) && !RoleEnum.SYSTEM.getRole().equals(role))){
            return ResultVO.fail("切片不存在");
        }
        Long knowledgeId = docsSlice.getKnowledgeId();

        Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
        if(knowledge==null){
            return ResultVO.fail("知识库不存在！");
        }
        String collectName = String.format(KnowledgeConstant.knowledgeName, knowledgeId);
        VectorStoreApi vectorStoreApi = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase()).getKnowledgeVectorStore(collectName,knowledge.getDimension());
        String vectorId = docsSlice.getVectorId();
        if(StringUtils.isNoneEmpty(vectorId)){
            vectorStoreApi.remove(vectorId);
        }
        return ResultVO.success();
    }

    /**
     * 向量化切片
     * 1. 删除已有
     * 2. 向量化 文本
     * 3. 将向量化的结果保存到 向量数据库
     * 4. 更新切片状态
     *
     * @param role
     * @return
     */
    public ResultVO embeddingSlice(Long userId,Long sliceId,String role){

        DocsSlice docsSlice = docsSliceService.getById(sliceId);
        if(docsSlice==null || (!docsSlice.getUserId().equals(userId) && !RoleEnum.SYSTEM.getRole().equals(role))){
            return ResultVO.fail("切片不存在");
        }
        if(docsSlice.getType().equals(DocsTypeEnum.KNOWLEDGE.name())){
            redisClient.sadd(KnowledgeConstant.contentInferQuestionSliceSet,String.valueOf(docsSlice.getId()));
            return ResultVO.success();
        }else if(docsSlice.getType().equals(DocsTypeEnum.PLAN.name())){
            redisClient.sadd(KnowledgeConstant.tileInferSliceSet,String.valueOf(docsSlice.getId()));
            return ResultVO.success();
        }
        // 普通切片则是走此逻辑
        Long knowledgeId = docsSlice.getKnowledgeId();
        Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
        if(knowledge==null){
            return ResultVO.fail("知识库不存在！");
        }
        String collectName = String.format(KnowledgeConstant.knowledgeName, knowledgeId);
        VectorStoreApi vectorStoreApi = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase()).getKnowledgeVectorStore(collectName,knowledge.getDimension());
        String vectorId = docsSlice.getVectorId();
        if(StringUtils.isNoneEmpty(vectorId)){
            vectorStoreApi.remove(vectorId);
        }
        String templateModel = knowledge.getTemplateModel();
        EmbeddingModel embed = embeddingProvider.embed(templateModel);
        Response<Embedding> embedded = embed.embed(docsSlice.getContent());

        Metadata metadata = Metadata.from(EmbedConst.SPLICE_ID, sliceId);
        TextSegment textSegment = TextSegment.from(docsSlice.getContent(), metadata);
        vectorId = vectorStoreApi.add(embedded.content(), textSegment);

        docsSlice = new DocsSlice();
        docsSlice.setId(sliceId);
        docsSlice.setVectorId(vectorId);
        docsSlice.setStatus(SliceEmbeddingStatus.EMBEDDING_ED.getStatus());
        boolean updated = docsSliceService.updateById(docsSlice);
        log.info("embeddingSlice sliceId={},vectorId={},updated={}",sliceId,vectorId,updated);
        return ResultVO.success();
    }





    /**
     * 向量化文档
     * 1. 删除已有的向量库里面的记录
     * 2. 新增切片数
     * 3. 切片向量化
     * @param userId
     * @param docsId
     * @param role
     * @return
     */
    public ResultVO embeddingDocs(Long userId,Long docsId,String role){

        KnowledgeDocs knowledgeDocs = knowledgeDocsService.getById(docsId);
        if(knowledgeDocs==null || (!knowledgeDocs.getUserId().equals(userId) && !RoleEnum.SYSTEM.getRole().equals(role))){
            return ResultVO.fail("文档不存在");
        }
        Long knowledgeId = knowledgeDocs.getKnowledgeId();
        Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
        if(knowledge==null){
            return ResultVO.fail("知识库不存在！");
        }
        Long sadd = redisClient.sadd(KnowledgeConstant.docsEmbeddingSet, String.valueOf(docsId));
        log.info("embeddingDocs docsId={},sadd={}",docsId,sadd);
        return ResultVO.success();
    }


    public ResultVO embeddingKnowledge(Long userId,Long knowledgeId,String role){

        Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
        if(knowledge==null){
            return ResultVO.fail("知识库不存在！");
        }

        LambdaQueryWrapper<KnowledgeDocs> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(KnowledgeDocs::getId);

        lambdaQueryWrapper.eq(KnowledgeDocs::getKnowledgeId,knowledgeId);
        List<KnowledgeDocs> knowledgeDocs = knowledgeDocsService.list(lambdaQueryWrapper);

        for (KnowledgeDocs knowledgeDoc : knowledgeDocs) {
            embeddingDocs(userId,knowledgeDoc.getId(),role);
        }
        return ResultVO.success();
    }



}
