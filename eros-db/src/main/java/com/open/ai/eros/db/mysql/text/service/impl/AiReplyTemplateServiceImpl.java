package com.open.ai.eros.db.mysql.text.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.open.ai.eros.db.mysql.text.entity.AiReplyTemplate;
import com.open.ai.eros.db.mysql.text.mapper.AiReplyTemplateMapper;
import com.open.ai.eros.db.mysql.text.service.IAiReplyTemplateService;
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
 * @since 2024-10-17
 */
@Slf4j
@Service
public class AiReplyTemplateServiceImpl extends ServiceImpl<AiReplyTemplateMapper, AiReplyTemplate> implements IAiReplyTemplateService {

    private final LoadingCache<Long, Optional<AiReplyTemplate>> REPLY_CACHE = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).
            initialCapacity(20).maximumSize(1000).build(new CacheLoader<Long, Optional<AiReplyTemplate>>() {

                @Override
                public Optional<AiReplyTemplate> load(Long aLong) throws Exception {
                    AiReplyTemplate template = getById(aLong);
                    if (template==null) {
                        return Optional.empty();
                    }
                    return Optional.of(template);
                }
            });

    public AiReplyTemplate getById(Long id){
        return this.baseMapper.selectById(id);
    }


    /**
     * 获取 自动回复模版
     *
     * @param id
     * @return
     */
    public String getCacheAiReplyTemplate(Long id){
        try {
            Optional<AiReplyTemplate> aiReplyTemplate = REPLY_CACHE.get(id);
            return aiReplyTemplate.map(AiReplyTemplate::getReplyContent).orElse(null);
        }catch (Exception e){
            log.error("getCacheAiReplyTemplate error id={}",id,e);
            AiReplyTemplate byId = getById(id);
            return byId==null?null:byId.getReplyContent();
        }
    }


}
