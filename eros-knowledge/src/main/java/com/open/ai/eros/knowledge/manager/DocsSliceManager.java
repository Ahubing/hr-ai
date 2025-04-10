package com.open.ai.eros.knowledge.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreApi;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreFactory;
import com.open.ai.eros.ai.vector.process.ContentInferQuestionAIProcess;
import com.open.ai.eros.ai.vector.process.DocsTitleAIProcess;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.constants.SliceEmbeddingStatus;
import com.open.ai.eros.db.mysql.knowledge.entity.DocsSlice;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.service.impl.DocsSliceServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeServiceImpl;
import com.open.ai.eros.knowledge.bean.req.DocsSliceAddReq;
import com.open.ai.eros.knowledge.bean.req.DocsSliceDeleteReq;
import com.open.ai.eros.knowledge.bean.req.DocsSliceSearchReq;
import com.open.ai.eros.knowledge.bean.req.DocsSliceUpdateReq;
import com.open.ai.eros.knowledge.bean.vo.DocsSliceVo;
import com.open.ai.eros.knowledge.convert.DocSliceConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @类名：DocsSliceManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/12 23:55
 */

@Slf4j
@Component
public class DocsSliceManager {


    @Autowired
    private DocsSliceServiceImpl docsSliceService;

    @Autowired
    private KnowledgeServiceImpl knowledgeService;


    @Autowired
    private ContentInferQuestionAIProcess contentInferQuestionAIProcess;

    @Autowired
    private DocsTitleAIProcess docsTitleAIProcess;


    public ResultVO<List<String>> getInferTitle(String title){
        List<String> titles = docsTitleAIProcess.getInferTitle(title);
        return ResultVO.success(titles);
    }


    public ResultVO<List<String>> getInferQuestion(String text){
        List<String> inferQuestion = contentInferQuestionAIProcess.getInferQuestion(text);
        return ResultVO.success(inferQuestion);
    }



    public ResultVO<List<String>> getInferQuestion(Long userId,Long id){
        DocsSlice docsSlice = docsSliceService.getById(id);
        if(docsSlice==null || !docsSlice.getUserId().equals(userId) ){
            return ResultVO.fail("切片不存在");
        }
        List<String> inferQuestion = contentInferQuestionAIProcess.getInferQuestion(docsSlice.getContent());
        return ResultVO.success(inferQuestion);
    }



    public ResultVO addDocsSlice(Long userId,DocsSliceAddReq req){

        DocsSlice docsSlice = DocSliceConvert.I.convertDocsSlice(req);
        docsSlice.setStatus(SliceEmbeddingStatus.UN_EMBEDDING.getStatus());
        docsSlice.setCreateTime(LocalDateTime.now());
        docsSlice.setUserId(userId);
        docsSlice.setWordNum(req.getContent().length());

        boolean saved = docsSliceService.save(docsSlice);
        log.info("addDocsSlice userId={}.name={},saved={}",userId,req.getName(),saved);
        if(!saved){
            throw new BizException("新增切片失败！");
        }
        return ResultVO.success();
    }



    public ResultVO updateDocsSlice(Long userId,DocsSliceUpdateReq req,String role){

        DocsSlice docsSlice = docsSliceService.getById(req.getId());
        if(docsSlice==null || (!docsSlice.getUserId().equals(userId) || !RoleEnum.SYSTEM.getRole().equals(role)) ){
            return ResultVO.fail("切片不存在");
        }
        docsSlice = DocSliceConvert.I.convertDocsSlice(req);
        docsSlice.setUserId(userId);
        boolean updated = docsSliceService.updateById(docsSlice);
        log.info("addDocsSlice userId={}.name={},saved={}",userId,req.getName(),updated);
        if(!updated){
            throw new BizException("修改切片失败！");
        }
        return ResultVO.success();
    }



    @Transactional
    public ResultVO deleteDocsSlice(Long userId,DocsSliceDeleteReq req,String role){
        Long docsId = req.getDocsId();
        Long id = req.getId();
        if(id!=null){
            deleteDocsSliceId(userId,id,role);
        }
        if(docsId!=null){
            deleteDocsSliceByDocId(userId,docsId,role);
        }
        return ResultVO.success();
    }


    /**
     * 根据向量id删除
     *
     * @param userId
     * @param role
     */

    @Transactional
    public void deleteDocsSliceId(Long userId,Long id,String role){
        DocsSlice docsSlice = docsSliceService.getById(id);
        if(docsSlice==null || (!docsSlice.getUserId().equals(userId) || !RoleEnum.SYSTEM.getRole().equals(role)) ){
            return;
        }
        String collectName = String.format(KnowledgeConstant.knowledgeName, docsSlice.getKnowledgeId());
        Knowledge knowledge = knowledgeService.getCacheById(docsSlice.getKnowledgeId());
        if(knowledge==null){
            return;
        }

        VectorStoreApi vectorStoreApi = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase()).getKnowledgeVectorStore(collectName,knowledge.getDimension());
        if(StringUtils.isNoneEmpty(docsSlice.getVectorId())){
            vectorStoreApi.remove(docsSlice.getVectorId());
        }
        docsSliceService.removeById(id);
    }


    @Transactional
    public void deleteDocsSliceByDocId(Long userId,Long docsId,String role){
        DocsSlice docsSlice = docsSliceService.getDocsSliceByDocsId(docsId);
        if(docsSlice==null || (!docsSlice.getUserId().equals(userId) || !RoleEnum.SYSTEM.getRole().equals(role)) ){
            return;
        }
        String collectName = String.format(KnowledgeConstant.knowledgeName, docsSlice.getKnowledgeId());
        List<String> serviceVectorIds = docsSliceService.getDocsVectorIds(docsId);
        if(CollectionUtils.isEmpty(serviceVectorIds)){
            return;
        }

        Knowledge knowledge = knowledgeService.getCacheById(docsSlice.getKnowledgeId());
        if(knowledge==null){
            return;
        }
        VectorStoreApi vectorStoreApi = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase()).getKnowledgeVectorStore(collectName,knowledge.getDimension());
        vectorStoreApi.removeAll(serviceVectorIds);
        docsSliceService.clearDocSlices(docsId);
    }



    public ResultVO<PageVO<DocsSliceVo>>list(DocsSliceSearchReq req){
        LambdaQueryWrapper<DocsSlice> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNoneEmpty(req.getKeyword())){
            lambdaQueryWrapper.like(DocsSlice::getName,req.getKeyword());
        }
        if(req.getUserId()!=null){
            lambdaQueryWrapper.eq(DocsSlice::getUserId,req.getUserId());
        }
        if(req.getKnowledgeId()!=null){
            lambdaQueryWrapper.eq(DocsSlice::getKnowledgeId,req.getKnowledgeId());
        }

        if(req.getDocsId()!=null){
            lambdaQueryWrapper.eq(DocsSlice::getDocsId,req.getDocsId());
        }
        if(req.getStatus()!=null){
            lambdaQueryWrapper.eq(DocsSlice::getStatus,req.getStatus());
        }

        lambdaQueryWrapper.orderByDesc(DocsSlice::getCreateTime);

        Page<DocsSlice> page = new Page<>(req.getPageNum(),req.getPageSize());
        Page<DocsSlice> knowledgePage = docsSliceService.page(page, lambdaQueryWrapper);
        List<DocsSliceVo> knowledgeVos = knowledgePage.getRecords().stream().map(DocSliceConvert.I::convertDocsSliceVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(knowledgePage.getTotal(),knowledgeVos));
    }



}
