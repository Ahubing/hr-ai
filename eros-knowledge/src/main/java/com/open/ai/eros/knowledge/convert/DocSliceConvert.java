package com.open.ai.eros.knowledge.convert;


import com.open.ai.eros.db.mysql.knowledge.entity.DocsSlice;
import com.open.ai.eros.knowledge.bean.req.DocsSliceAddReq;
import com.open.ai.eros.knowledge.bean.req.DocsSliceUpdateReq;
import com.open.ai.eros.knowledge.bean.vo.DocsSliceVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DocSliceConvert {


    DocSliceConvert I = Mappers.getMapper(DocSliceConvert.class);


    DocsSliceVo convertDocsSliceVo(DocsSlice docsSlice);



    DocsSlice convertDocsSlice(DocsSliceAddReq req);

    DocsSlice convertDocsSlice(DocsSliceUpdateReq req);


}
