package com.open.ai.eros.ai.model.processor.ai;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @类名：AIProxyServiceFactory
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/18 12:54
 */
@Slf4j
public class AIProxyServiceFactory {

    private static Cache<Long, AIProxyService> serviceMap =
            CacheBuilder.newBuilder().initialCapacity(100).maximumSize(5000).expireAfterWrite(10, TimeUnit.DAYS).build();




    public static AIProxyService getProxyService(ModelConfigVo modelConfigVo){
        try {
            AIProxyService service = serviceMap.get(modelConfigVo.getId(), () -> createModelService(modelConfigVo));
            if(service==null){
                return null;
            }
            /**
             * 如果初始化的url 和 token一致 直接返回
             */
            if(service.getBaseUrl().equals(modelConfigVo.getBaseUrl()) && service.getToken().equals(modelConfigVo.getToken())){
                return service;
            }else{
                service = createModelService(modelConfigVo);
                //发生变动了
                if(service!=null) serviceMap.put(modelConfigVo.getId(),service);
            }
            return service;
        }catch (Exception e){
            log.error("getProxyService error modelConfigVo={} ",modelConfigVo,e);
            return createModelService(modelConfigVo);
        }
    }


    public static AIProxyService createModelService(ModelConfigVo modelConfigVo){
        try {
            return new AIProxyService(modelConfigVo.getBaseUrl(),modelConfigVo.getToken());
        }catch (Exception e){
            // 在这里加上catch 防止初始化一个key有问题 从而影响其它的key了
            log.error("initGptModelConfig error modelConfig={}",modelConfigVo,e);
        }
        return null;
    }



}
