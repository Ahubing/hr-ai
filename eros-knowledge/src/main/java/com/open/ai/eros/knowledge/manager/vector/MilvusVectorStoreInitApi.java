package com.open.ai.eros.knowledge.manager.vector;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.ai.lang.chain.bean.CollectionVo;
import com.open.ai.eros.ai.lang.chain.vector.VectorStoreInitApi;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.db.constants.KnowledgeConstant;
import com.open.ai.eros.db.constants.VectorStoreEnum;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.service.impl.KnowledgeServiceImpl;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @类名：KnowledgeMilvusVectorStoreInitApi
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/14 14:14
 */

@Component
public class MilvusVectorStoreInitApi extends VectorStoreInitApi {


    @Autowired
    private KnowledgeServiceImpl knowledgeService;



    @Override
    public List<CollectionVo> getCollectionName() {
        LambdaQueryWrapper<Knowledge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Knowledge::getVectorDatabase,vectorStoreType().getVector());
        List<Knowledge> knowledges = knowledgeService.list(lambdaQueryWrapper);
        return knowledges.stream().map(e->{
            String collectName = String.format(KnowledgeConstant.knowledgeName, e.getId());
            String templateModel = e.getTemplateModel();
            String[] split = templateModel.split(":");
            String model = split[1];
            Integer dimension = OpenAiEmbeddingModelName.knownDimension(model);
            if(dimension==null){
                throw new BizException("不支持的open ai的矢量长度");
            }
            return CollectionVo.builder().collectionName(collectName)
                    .dimension(dimension).build();
        }).collect(Collectors.toList());
    }

    @Override
    public VectorStoreEnum vectorStoreType() {
        return VectorStoreEnum.MILVUS;
    }
}
