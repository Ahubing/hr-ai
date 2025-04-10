package com.open.ai.eros.knowledge.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreApi;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreFactory;
import com.open.ai.eros.ai.manager.ThreadPoolManager;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.util.HttpFileUtil;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.entity.KnowledgeDocs;
import com.open.ai.eros.db.mysql.knowledge.service.impl.DocsSliceServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeDocsServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeServiceImpl;
import com.open.ai.eros.knowledge.bean.req.KnowledgeDocsAddReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeDocsSearchReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeDocsUpdateReq;
import com.open.ai.eros.knowledge.bean.vo.KnowledgeDocsVo;
import com.open.ai.eros.knowledge.convert.KnowledgeDocsConvert;
import com.open.ai.eros.knowledge.util.PDFImageExtractor;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.transformer.jsoup.HtmlToTextDocumentTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @类名：KnowledgeDocsManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/12 21:56
 */

@Slf4j
@Component
public class KnowledgeDocsManager {


    @Autowired
    private KnowledgeDocsServiceImpl knowledgeDocsService;


    @Autowired
    private DocsSliceServiceImpl docsSliceService;

    @Autowired
    private KnowledgeServiceImpl knowledgeService;



    public ResultVO<KnowledgeDocsVo> findById(Long userId,Long id){
        KnowledgeDocs knowledgeDocs = knowledgeDocsService.getById(id);
        if(knowledgeDocs==null || !knowledgeDocs.getUserId().equals(userId)){
            return ResultVO.fail("文档不存在");
        }
        KnowledgeDocsVo knowledgeDocsVo = KnowledgeDocsConvert.I.convertKnowledgeDocsVo(knowledgeDocs);
        return ResultVO.success(knowledgeDocsVo);
    }


    @Transactional
    public ResultVO deleteKnowledgeDocs(Long userId,Long id,String role){
        KnowledgeDocs knowledgeDocs = knowledgeDocsService.getById(id);
        if(knowledgeDocs==null || (!knowledgeDocs.getUserId().equals(userId) && !RoleEnum.SYSTEM.getRole().equals(role))){
            return ResultVO.fail("文档不存在");
        }
        Long knowledgeId = knowledgeDocs.getKnowledgeId();

        Knowledge knowledge = knowledgeService.getCacheById(knowledgeId);
        if(knowledge==null){
            return ResultVO.fail("知识库不存在！");
        }

        String collectName = String.format(KnowledgeConstant.knowledgeName, knowledgeId);

        VectorStoreApi vectorStoreApi = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase()).getKnowledgeVectorStore(collectName,knowledge.getDimension());
        List<String> vectorIds = docsSliceService.getDocsVectorIds(id);
        if(CollectionUtils.isNotEmpty(vectorIds)){
            vectorStoreApi.removeAll(vectorIds);
        }
        docsSliceService.clearDocSlices(id);
        boolean removed = knowledgeDocsService.removeById(id);
        log.info("deleteKnowledgeDocs removed={},id={}",removed,id);
        if(!removed){
            throw new BizException("删除失败");
        }
        return ResultVO.success();
    }


    /**
     * 新增文档
     *
     * @param userId
     * @param req
     * @return
     */
    @Transactional
    public ResultVO addKnowledgeDocs(long userId, KnowledgeDocsAddReq req) {
        ThreadPoolManager.docsSlicePool.execute(() -> {
            KnowledgeDocs knowledgeDocs = KnowledgeDocsConvert.I.convertKnowledgeDocs(req);
            knowledgeDocs.setUserId(userId);
            knowledgeDocs.setCreateTime(LocalDateTime.now());

            String url = knowledgeDocs.getUrl();
            List<KnowledgeDocs> knowledgeDocsList = new ArrayList<>();
            try {
                if (StringUtils.isNotEmpty(url)) {
                    String[] splitUrls = url.split(",");
                    for (String innerUrl : splitUrls) {
                        KnowledgeDocs clonedDocs = cloneKnowledgeDocs(knowledgeDocs);
                        clonedDocs.setUrl(innerUrl);

                        String text = loadDocumentFromUrl(innerUrl);
                        clonedDocs.setContent(text);
                        validateContent(clonedDocs.getContent());
                        knowledgeDocsList.add(clonedDocs);
                    }
                    knowledgeDocsService.saveBatch(knowledgeDocsList);
                } else {
                    knowledgeDocs.setContent(req.getContent());
                    validateContent(knowledgeDocs.getContent());
                    boolean saved = knowledgeDocsService.save(knowledgeDocs);
                    log.info("addKnowledgeDocs saved={}, name={}", saved, req.getName());
                }
            } catch (Exception e) {
                log.error("addKnowledgeDocs error req = {}", JSONObject.toJSONString(req), e);
                throw new BizException("上传文档异常!");
            }

        });
        return ResultVO.success();
    }

    private KnowledgeDocs cloneKnowledgeDocs(KnowledgeDocs knowledgeDocs) {
        KnowledgeDocs knowledge = new KnowledgeDocs();
        knowledge.setName(knowledgeDocs.getName());
        knowledge.setKnowledgeId(knowledgeDocs.getKnowledgeId());
        knowledge.setType(knowledgeDocs.getType());
        knowledge.setUrl(knowledgeDocs.getUrl());
        knowledge.setSliceNum(knowledgeDocs.getSliceNum());
        knowledge.setSliceStatus(knowledgeDocs.getSliceStatus());
        knowledge.setContent(knowledgeDocs.getContent());
        knowledge.setCreateTime(knowledgeDocs.getCreateTime());
        knowledge.setUserId(knowledgeDocs.getUserId());
        knowledge.setSliceRule(knowledgeDocs.getSliceRule());
         // Returning the cloned object
        return knowledge;
    }

    private String loadDocumentFromUrl(String url) throws IOException {
        if (url.startsWith("http")) {
            File file = HttpFileUtil.downloadFile(url);
            if(url.endsWith(".pdf")){
                return PDFImageExtractor.getPdfText(file.getAbsolutePath());
            }
            Document document = FileSystemDocumentLoader.loadDocument(file.getAbsolutePath(), new ApacheTikaDocumentParser());
            if(url.endsWith(".html")){
                HtmlToTextDocumentTransformer htmlToTextDocumentTransformer = new HtmlToTextDocumentTransformer();
                Document transform = htmlToTextDocumentTransformer.transform(document);
                return transform.text();
            }
            return document.text();
        }
        return FileSystemDocumentLoader.loadDocument(url, new ApacheTikaDocumentParser()).text();
    }

    private void validateContent(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new BizException("文档内容不能为空");
        }
    }


    public ResultVO updateKnowledgeDocs(long userId, KnowledgeDocsUpdateReq req,String role) {
        Long id = req.getId();
        KnowledgeDocs knowledgeDocs = knowledgeDocsService.getById(id);
        if(knowledgeDocs==null || (!knowledgeDocs.getUserId().equals(userId) && !RoleEnum.SYSTEM.getRole().equals(role))){
            return ResultVO.fail("文档不存在");
        }
        knowledgeDocs = KnowledgeDocsConvert.I.convertKnowledgeDocs(req);
        boolean updated = knowledgeDocsService.updateById(knowledgeDocs);
        log.info("updateKnowledgeDocs removed={},name={}",updated,req.getName());
        return updated?ResultVO.success():ResultVO.fail("修改失败！");
    }



    public ResultVO<PageVO<KnowledgeDocsVo>> searchDocs(KnowledgeDocsSearchReq req){
        LambdaQueryWrapper<KnowledgeDocs> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNoneEmpty(req.getKeyword())){
            lambdaQueryWrapper.like(KnowledgeDocs::getName,req.getKeyword());
        }
        if(req.getUserId()!=null){
            lambdaQueryWrapper.eq(KnowledgeDocs::getUserId,req.getUserId());
        }
        if(req.getKnowledgeId()!=null){
            lambdaQueryWrapper.eq(KnowledgeDocs::getKnowledgeId,req.getKnowledgeId());
        }
        if(req.getId()!=null){
            lambdaQueryWrapper.eq(KnowledgeDocs::getId,req.getId());
        }
        Page<KnowledgeDocs> page = new Page<>(req.getPageNum(),req.getPageSize());
        Page<KnowledgeDocs> knowledgePage = knowledgeDocsService.page(page, lambdaQueryWrapper);
        List<KnowledgeDocsVo> knowledgeVos = knowledgePage.getRecords().stream().map(KnowledgeDocsConvert.I::convertKnowledgeDocsVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(knowledgePage.getTotal(),knowledgeVos));
    }




}
