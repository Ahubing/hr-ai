package com.open.ai.eros.knowledge.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.ai.constatns.EmbeddingModelTemplateEnum;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreApi;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreFactory;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.constants.VectorStoreEnum;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeServiceImpl;
import com.open.ai.eros.knowledge.bean.req.KnowledgeAddReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeSearchReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeUpdateReq;
import com.open.ai.eros.knowledge.bean.vo.KnowledgeSimpleVo;
import com.open.ai.eros.knowledge.bean.vo.KnowledgeVo;
import com.open.ai.eros.knowledge.convert.KnowledgeConvert;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @类名：KnowledgeManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/12 18:12
 */

@Slf4j
@Component
public class KnowledgeManager {


    @Autowired
    private KnowledgeServiceImpl knowledgeService;



    public ResultVO<List<KnowledgeSimpleVo>> simpleList(Long userId){
        LambdaQueryWrapper<Knowledge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(userId!=null){
            lambdaQueryWrapper.eq(Knowledge::getUserId,userId);
        }
        lambdaQueryWrapper.select(Knowledge::getId,Knowledge::getName);
        List<Knowledge> knowledge = knowledgeService.list(lambdaQueryWrapper);
        List<KnowledgeSimpleVo> knowledgeSimpleVos = knowledge.stream().map(e -> KnowledgeSimpleVo.builder().id(e.getId()).name(e.getName()).build()).collect(Collectors.toList());
        return ResultVO.success(knowledgeSimpleVos);
    }


    public ResultVO<PageVO<KnowledgeVo>> list(KnowledgeSearchReq req){

        LambdaQueryWrapper<Knowledge> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNoneEmpty(req.getKeyword())){
            lambdaQueryWrapper.like(Knowledge::getName,req.getKeyword());
        }
        if(req.getUserId()!=null){
            lambdaQueryWrapper.eq(Knowledge::getUserId,req.getUserId());
        }
        Page<Knowledge> page = new Page<>(req.getPageNum(),req.getPageSize());
        Page<Knowledge> knowledgePage = knowledgeService.page(page, lambdaQueryWrapper);
        List<KnowledgeVo> knowledgeVos = knowledgePage.getRecords().stream().map(KnowledgeConvert.I::convertKnowledgeVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(knowledgePage.getTotal(),knowledgeVos));
    }



    public ResultVO  updateKnowledge(Long userId,KnowledgeUpdateReq req,String role){

        Long id = req.getId();
        Knowledge knowledge = knowledgeService.getCacheById(id);
        if(knowledge==null || (!knowledge.getUserId().equals(userId) &&!role.equals(RoleEnum.SYSTEM.getRole()))){
            return ResultVO.fail("该知识库不存在或者无权操作！");
        }

        knowledge = KnowledgeConvert.I.convertKnowledge(req);
        boolean updated = knowledgeService.updateById(knowledge);
        log.info("updateKnowledge updated={},knowledge={}",updated, JSONObject.toJSONString(knowledge));
        return updated?ResultVO.success():ResultVO.fail("更新失败");
    }


    /**
     * 1. 新增知识库
     * 2. 向量集合
     * @param userId
     * @param req
     * @return
     */
    @Transactional
    public ResultVO addKnowledge(Long userId, KnowledgeAddReq req){
        Knowledge knowledge = KnowledgeConvert.I.convertKnowledge(req);
        knowledge.setCreateTime(LocalDateTime.now());
        knowledge.setUserId(userId);

        if(StringUtils.isEmpty(knowledge.getVectorDatabase())){
            knowledge.setVectorDatabase(VectorStoreEnum.MILVUS.getVector());
        }

        if(StringUtils.isEmpty(knowledge.getTemplateModel())){
            String templateModel = EmbeddingModelTemplateEnum.OPEN_AI_API_GPT.getTemplate() + ":" + EmbeddingModelTemplateEnum.OPEN_AI_API_GPT.getModels().get(0);
            knowledge.setTemplateModel(templateModel);
        }

        String templateModel = knowledge.getTemplateModel();
        String[] split = templateModel.split(":");
        String model = split[1];

        Integer dimension = OpenAiEmbeddingModelName.knownDimension(model);
        if(dimension==null){
            throw new BizException("不支持的open ai的矢量长度");
        }
        knowledge.setDimension(dimension);
        boolean saved = knowledgeService.save(knowledge);
        log.info("addKnowledge saved={},knowledge={}",saved, JSONObject.toJSONString(knowledge));
        if(saved){
            String collectName = String.format(KnowledgeConstant.knowledgeName, knowledge.getId());
            VectorStoreApi embeddingStore = VectorStoreFactory.getVectorStoreApi(knowledge.getVectorDatabase());

            embeddingStore.createCollectionVectorStore(collectName,dimension);
        }
        return saved?ResultVO.success():ResultVO.fail("更新失败");
    }


}
