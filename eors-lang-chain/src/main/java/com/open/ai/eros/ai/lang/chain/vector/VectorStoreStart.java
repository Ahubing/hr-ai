package com.open.ai.eros.ai.lang.chain.vector;

import com.open.ai.eros.ai.lang.chain.bean.CollectionVo;
import com.open.ai.eros.db.constants.VectorStoreEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @类名：VectorStoreStart
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/14 17:46
 */
@Slf4j
@Order(9999)
@Component
public class VectorStoreStart {

    @Autowired
    private List<VectorStoreInitApi> vectorStoreInitApis;

    @Autowired
    private List<VectorStoreApi> vectorStoreApis;

    @PostConstruct
    public void initVectorStore(){
        log.info("开始初始化知识库向量库------");
        try {
            for (VectorStoreInitApi vectorStoreInitApi : vectorStoreInitApis) {
                List<CollectionVo> collectionVos = vectorStoreInitApi.getCollectionName();
                if(CollectionUtils.isEmpty(collectionVos)){
                    continue;
                }
                VectorStoreEnum vectorStoreEnum = vectorStoreInitApi.vectorStoreType();
                VectorStoreApi vectorStoreApi = VectorStoreFactory.getVectorStoreApi(vectorStoreEnum.getVector());
                for (CollectionVo collectionVo : collectionVos) {
                    if(collectionVo.getDimension()==null || collectionVo.getDimension()<=0){
                        continue;
                    }
                    if(vectorStoreApi==null){
                        continue;
                    }
                    vectorStoreApi.createCollectionVectorStore(collectionVo.getCollectionName(),collectionVo.getDimension());
                }
            }
        }catch (Exception e){
                log.error("初始化失败",e);
        }

        log.info("初始化知识库向量库结束------");

    }


}
