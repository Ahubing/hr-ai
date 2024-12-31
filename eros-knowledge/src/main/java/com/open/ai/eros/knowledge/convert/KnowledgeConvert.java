package com.open.ai.eros.knowledge.convert;


import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.knowledge.bean.req.KnowledgeAddReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeUpdateReq;
import com.open.ai.eros.knowledge.bean.vo.KnowledgeVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface KnowledgeConvert {


    KnowledgeConvert I = Mappers.getMapper(KnowledgeConvert.class);


    KnowledgeVo convertKnowledgeVo(Knowledge knowledge);


    Knowledge convertKnowledge(KnowledgeAddReq req);


    Knowledge convertKnowledge(KnowledgeUpdateReq req);



}
