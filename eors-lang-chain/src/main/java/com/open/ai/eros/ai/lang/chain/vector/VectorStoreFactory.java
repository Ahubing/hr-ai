package com.open.ai.eros.ai.lang.chain.vector;

import com.open.ai.eros.db.constants.VectorStoreEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 向量数据库的工厂类
 *
 *
 * @类名：VectorStoreFactory
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/13 19:01
 */
public class VectorStoreFactory {

    private static Map<String, VectorStoreApi> embeddingStoreMap = new ConcurrentHashMap<>();

    public static void setEmbeddingStore(String vectorStore, VectorStoreApi vectorStoreApi){

        VectorStoreApi old = VectorStoreFactory.getVectorStoreApi(vectorStore);
        if(old==null){
            synchronized (VectorStoreFactory.class){
                old = VectorStoreFactory.getVectorStoreApi(vectorStore);
                if(old==null){
                    embeddingStoreMap.put(vectorStore, vectorStoreApi);
                }
            }
        }
    }

    /**
     * 获取默认的 向量操作类
     *
     * @return
     */
    public static VectorStoreApi getVectorStoreApi(){
        return getVectorStoreApi(VectorStoreEnum.MILVUS.getVector());
    }

    public static VectorStoreApi getVectorStoreApi(String vectorStore){
        if(StringUtils.isEmpty(vectorStore)){
            vectorStore = VectorStoreEnum.MILVUS.getVector();
        }
        return embeddingStoreMap.get(vectorStore);
    }

}
