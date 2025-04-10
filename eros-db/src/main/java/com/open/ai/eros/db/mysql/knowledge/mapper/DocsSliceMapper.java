package com.open.ai.eros.db.mysql.knowledge.mapper;

import com.open.ai.eros.db.mysql.knowledge.entity.DocsSlice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 文档切片表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@Mapper
public interface DocsSliceMapper extends BaseMapper<DocsSlice> {



    @Select(" select  * from  docs_slice where  vector_id = #{vectorId} limit 1 ")
    DocsSlice getDocsSliceByVectorId(@Param("vectorId") String vectorId);



    @Select(" select  * from  docs_slice where docs_id = #{docsId} limit 1 ")
    DocsSlice getDocsSliceByDocsId(@Param("docsId") Long docsId);



    @Delete(" delete from docs_slice  where vector_id = #{vectorId} and user_id =  #{userId} limit 1 ")
    int deleteByVectorId( @Param("userId") Long userId,@Param("vectorId") String vectorId);


    @Delete("delete from docs_slice  where docs_id =#{docsId} ")
    int clearDocSlices(@Param("docsId") Long docsId);


}
