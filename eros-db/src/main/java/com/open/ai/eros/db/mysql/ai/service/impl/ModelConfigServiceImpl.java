package com.open.ai.eros.db.mysql.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfig;
import com.open.ai.eros.db.mysql.ai.mapper.ModelConfigMapper;
import com.open.ai.eros.db.mysql.ai.service.IModelConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-04
 */
@Slf4j
@Service
public class ModelConfigServiceImpl extends ServiceImpl<ModelConfigMapper, ModelConfig> implements IModelConfigService {



    private final LoadingCache<Long, Optional<ModelConfig>> MODEL_CONFIG_CACHE = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).
            initialCapacity(20).maximumSize(1000).build(new CacheLoader<Long, Optional<ModelConfig>>() {

                @Override
                public Optional<ModelConfig> load(Long aLong) throws Exception {
                    ModelConfig modelConfig = getById(aLong);
                    if(modelConfig==null){
                        return Optional.empty();
                    }
                    return Optional.of(modelConfig);
                }
            });


    public ModelConfig getById(Long id){
        return super.getById(id);
    }


    public ModelConfig getCacheById(Long id){
        try {
            Optional<ModelConfig> modelConfigOptional = MODEL_CONFIG_CACHE.get(id);
            if(modelConfigOptional.isPresent()){
                return modelConfigOptional.get();
            }
        }catch (Exception e){
            log.error("getCacheById error id={} ",id,e);
            return super.getById(id);
        }
        return null;
    }




    public int updateUsedBalance(Long id,Long cost){
        return this.getBaseMapper().updateUsedBalance(id,cost);
    }
}
