package com.open.ai.eros.db.mysql.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.knowledge.entity.DocsSlice;
import com.open.ai.eros.db.mysql.knowledge.mapper.DocsSliceMapper;
import com.open.ai.eros.db.mysql.knowledge.service.IDocsSliceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 文档切片表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@Slf4j
@Service
public class DocsSliceServiceImpl extends ServiceImpl<DocsSliceMapper, DocsSlice> implements IDocsSliceService {


    public boolean deleteByVectorId(Long userId,String vectorId){
        return this.getBaseMapper().deleteByVectorId(userId,vectorId)>0;
    }



    public DocsSlice getDocsSliceByDocsId(Long docsId){
        return this.getBaseMapper().getDocsSliceByDocsId(docsId);
    }



    public DocsSlice getDocsSliceByVectorId(String vectorId){
        return this.getBaseMapper().getDocsSliceByVectorId(vectorId);
    }


    public List<String> getDocsVectorIds(Long docsId){
        LambdaQueryWrapper<DocsSlice> selectWrapper = Wrappers.<DocsSlice>lambdaQuery()
                .select(DocsSlice::getVectorId)
                .eq(DocsSlice::getDocsId, docsId);
        List<String> vectorIds = this.getBaseMapper().selectList(selectWrapper)
                .stream()
                .map(DocsSlice::getVectorId)
                .collect(Collectors.toList());
        log.debug("getVectorIds of doc: docsId={}, count ={} ", docsId, vectorIds.size());
        return vectorIds;
    }



    public boolean clearDocSlices(Long docsId) {
        return this.getBaseMapper().clearDocSlices(docsId)>0;
    }

}
