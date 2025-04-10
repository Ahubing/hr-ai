package com.open.ai.eros.db.mysql.knowledge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.mapper.KnowledgeMapper;
import com.open.ai.eros.db.mysql.knowledge.service.IKnowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@Slf4j
@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge> implements IKnowledgeService {


    private final LoadingCache<Long, Knowledge> knowledgeCache = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).
            initialCapacity(20).maximumSize(1000).build(new CacheLoader<Long, Knowledge>() {

                @Override
                public Knowledge load(Long aLong) throws Exception {
                    return getById(aLong);
                }
            });




    public Knowledge getById(Long id){
        return super.getById(id);
    }


    public Knowledge getCacheById(Long id){
        try {
            return knowledgeCache.get(id);
        }catch (Exception e){
            log.error("getCacheById error id={}",id,e);
            return getById(id);
        }
    }


}
