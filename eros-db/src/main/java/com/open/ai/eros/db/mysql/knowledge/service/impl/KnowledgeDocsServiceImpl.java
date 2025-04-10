package com.open.ai.eros.db.mysql.knowledge.service.impl;

import com.open.ai.eros.db.mysql.knowledge.entity.KnowledgeDocs;
import com.open.ai.eros.db.mysql.knowledge.mapper.KnowledgeDocsMapper;
import com.open.ai.eros.db.mysql.knowledge.service.IKnowledgeDocsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@Service
public class KnowledgeDocsServiceImpl extends ServiceImpl<KnowledgeDocsMapper, KnowledgeDocs> implements IKnowledgeDocsService {


    public boolean updateKnowledgeDocsSliceStatusAndNum(Long id,Integer status,Integer num){
        KnowledgeDocs knowledgeDocs = new KnowledgeDocs();
        knowledgeDocs.setId(id);
        knowledgeDocs.setSliceNum(num);
        knowledgeDocs.setSliceStatus(status);
        return this.getBaseMapper().updateById(knowledgeDocs)>0;
    }


}
