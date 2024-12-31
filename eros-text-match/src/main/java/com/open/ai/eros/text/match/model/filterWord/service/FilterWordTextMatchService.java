package com.open.ai.eros.text.match.model.filterWord.service;


import com.open.ai.eros.db.mysql.text.entity.FilterWordInfo;
import com.open.ai.eros.db.mysql.text.entity.FilterWordInfoVo;
import com.open.ai.eros.db.mysql.text.mapper.FilterWordMapper;
import com.open.ai.eros.db.mysql.text.service.impl.AiReplyTemplateServiceImpl;
import com.open.ai.eros.text.match.constants.FilterWordConstant;
import com.open.ai.eros.text.match.model.filterWord.base.FilterWordServer;
import com.open.ai.eros.text.match.model.filterWord.base.FilterWordServerFactory;
import com.open.ai.eros.text.match.model.filterWord.bean.req.FilterWordAddReq;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.FilterWordResultVo;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.HitTextDetail;
import com.open.ai.eros.text.match.model.filterWord.bean.vo.TextMatchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class FilterWordTextMatchService {

    @Autowired
    private FilterWordServerFactory filterWordServerFactory;

    @Resource
    private FilterWordMapper filterWordMapper;

    private String filter_word_discern_model = "filter_word";

    @Autowired
    private AiReplyTemplateServiceImpl aiReplyTemplateService;

    private TextMatchResult defaultResult = new TextMatchResult(filter_word_discern_model);

    /**
     * 敏感词匹配
     * @param text
     * @param channelId
     * @return
     */
    public TextMatchResult filterWordTextMatch(long channelId, String text){
        FilterWordServer filterWordServer = filterWordServerFactory.getFilterWordServer();
        if(filterWordServer==null){
            return defaultResult;
        }
        Set<FilterWordResultVo> resultVos = filterWordServer.generalMatching(text, channelId);
        if(CollectionUtils.isEmpty(resultVos)){
            return defaultResult;
        }
        TextMatchResult result = new TextMatchResult(filter_word_discern_model);

        result.setHitTextDetails(new ArrayList<>());

        for (FilterWordResultVo resultVo : resultVos) {
            if(resultVo.getType() == FilterWordConstant.WRITE_WORD){
                //过滤掉白词
                continue;
            }
            HitTextDetail.HitTextDetailBuilder builder = HitTextDetail.builder();
            if(resultVo.getType() == FilterWordConstant.REPLY_TYPE){
                String cacheAiReplyTemplate = aiReplyTemplateService.getCacheAiReplyTemplate(resultVo.getReplyId());
                if(!StringUtils.isEmpty(cacheAiReplyTemplate)){
                    builder.replyTemplate(cacheAiReplyTemplate);
                }
            }
            builder.hitText(resultVo.getWordContent());
            builder.id(resultVo.getId());
            if(!result.isHit()){
                result.setHit(true);
            }
            result.getHitTextDetails().add(builder.build());
        }
        return result;
    }


    public boolean addFilterWord(FilterWordAddReq req,Long userId){
        FilterWordInfo entity = new FilterWordInfo();
        entity.setWordContent(req.getWordContent());
        entity.setChannelStr(buildChannelStr(req.getChannelIds()));
        entity.setLanguage(req.getLanguage());
        entity.setType(req.getType());
        entity.setRiskLevel(req.getRiskLevel());
        entity.setCreateUserId(userId);
        entity.setUpdateUserId(userId);
        entity.setRiskType(req.getRiskType());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        boolean result = filterWordMapper.insert(entity)>0;
        if(result){
            FilterWordServer filterWordServer = filterWordServerFactory.getFilterWordServer();
            if(filterWordServer!=null){
                FilterWordInfoVo addFilterWord = new FilterWordInfoVo();
                BeanUtils.copyProperties(entity,addFilterWord);
                filterWordServer.addNewWord(addFilterWord);
            }
        }
        return result;
    }


    private String buildChannelStr(List<Long> channelIds){
        if(CollectionUtils.isEmpty(channelIds)){
            return ",,";
        }
        String channelStr = ",";
        for (Long channelId : channelIds) {
            channelStr += channelId+",";
        }
        return channelStr;
    }


}
