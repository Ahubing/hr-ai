package com.open.ai.eros.db.mysql.text.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.open.ai.eros.db.mysql.text.entity.FilterWordInfo;
import com.open.ai.eros.db.mysql.text.mapper.FilterWordMapper;
import com.open.ai.eros.db.mysql.text.service.IFilterWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class FilterWordServiceImpl extends ServiceImpl<FilterWordMapper, FilterWordInfo> implements IFilterWordService {


    public int deleteReplyId(Long replyId,Long userId){
        return this.getBaseMapper().deleteReplyId(replyId,userId);
    }

    public List<FilterWordInfo> getByReplyId(Long replyId, Long userId){
        return this.getBaseMapper().getByReplyId(replyId,userId);
    }

}
