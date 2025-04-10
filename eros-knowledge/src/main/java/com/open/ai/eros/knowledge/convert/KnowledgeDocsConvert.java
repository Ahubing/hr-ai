package com.open.ai.eros.knowledge.convert;


import com.open.ai.eros.db.mysql.knowledge.entity.KnowledgeDocs;
import com.open.ai.eros.knowledge.bean.req.KnowledgeDocsAddReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeDocsUpdateReq;
import com.open.ai.eros.knowledge.bean.vo.KnowledgeDocsVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface KnowledgeDocsConvert {


    KnowledgeDocsConvert I = Mappers.getMapper(KnowledgeDocsConvert.class);

    KnowledgeDocsVo convertKnowledgeDocsVo(KnowledgeDocs knowledgeDocs);

    KnowledgeDocs convertKnowledgeDocs(KnowledgeDocsAddReq req);

    KnowledgeDocs convertKnowledgeDocs(KnowledgeDocsUpdateReq req);

}
