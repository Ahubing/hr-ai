package com.open.ai.eros.text.match.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.text.entity.AiReplyTemplate;
import com.open.ai.eros.db.mysql.text.entity.FilterWordInfo;
import com.open.ai.eros.db.mysql.text.service.impl.AiReplyTemplateServiceImpl;
import com.open.ai.eros.db.mysql.text.service.impl.FilterWordServiceImpl;
import com.open.ai.eros.text.match.bean.AIReplyTemplateAddReq;
import com.open.ai.eros.text.match.bean.AIReplyTemplateSearchReq;
import com.open.ai.eros.text.match.bean.AIReplyTemplateUpdateReq;
import com.open.ai.eros.text.match.bean.AiReplyTemplateVo;
import com.open.ai.eros.text.match.convert.AiReplyTemplateConvert;
import com.open.ai.eros.text.match.model.filterWord.base.FilterWordServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @类名：AIReplyTemplateManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/20 1:18
 */
@Slf4j
@Component
public class AIReplyTemplateManager {


    @Autowired
    private AiReplyTemplateServiceImpl aiReplyTemplateService;

    @Autowired
    private FilterWordServiceImpl filterWordService;



    public ResultVO<PageVO<AiReplyTemplateVo>> search(AIReplyTemplateSearchReq req, Long userId){
        LambdaQueryWrapper<AiReplyTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Page<AiReplyTemplate> page = new Page<>(req.getPage(),req.getPageSize());
        if(StringUtils.isNoneEmpty(req.getKeyWord())){
            lambdaQueryWrapper.like(AiReplyTemplate::getReplyContent,"%"+req.getKeyWord()+"%");
        }
        lambdaQueryWrapper.eq(AiReplyTemplate::getUserId,userId);
        Page<AiReplyTemplate> templatePage = aiReplyTemplateService.page(page, lambdaQueryWrapper);
        List<AiReplyTemplateVo> aiReplyTemplateVos = templatePage.getRecords().stream().map(e->{
            // 更换成手动的方式
            AiReplyTemplateVo aiReplyTemplateVo = convertAiReplyTemplateVo(e);
            List<FilterWordInfo> filterWordInfos = filterWordService.getByReplyId(e.getId(), userId);
            if(CollectionUtils.isNotEmpty(filterWordInfos)){
                Set<Long> channelIds = new HashSet<>();
                Set<String> wordContents = new HashSet<>();
                for (FilterWordInfo wordInfo : filterWordInfos) {
                    channelIds.addAll(FilterWordServer.getChannelIds(wordInfo.getChannelStr()));
                    wordContents.add(wordInfo.getWordContent());
                }
                aiReplyTemplateVo.setWordContents(wordContents);
                aiReplyTemplateVo.setChannelIds(channelIds);
            }
            return aiReplyTemplateVo;
        }).collect(Collectors.toList());

        return ResultVO.success(PageVO.build(templatePage.getTotal(),aiReplyTemplateVos));
    }

    /**
     * 模版绑定通道
     *
     * @param channelId
     * @param userId
     * @return
     */
    public ResultVO bindChannel(Long replyId, Long channelId,Long userId){

        //获取敏感词
        List<FilterWordInfo> infoVos = filterWordService.getByReplyId(replyId, userId);
        if(CollectionUtils.isNotEmpty(infoVos)){
            buildFilterWordChannel(infoVos,channelId);
            filterWordService.updateBatchById(infoVos,10000);
        }
        return ResultVO.success();
    }

    private void buildFilterWordChannel(List<FilterWordInfo> infoVos,Long channelId){
        for (FilterWordInfo infoVo : infoVos) {
            if(channelId==null){
                // 通道id为null 删除下面所有的信息
                infoVo.setChannelStr(",,");
                continue;
            }
            Set<Long> channelIds = FilterWordServer.getChannelIds(infoVo.getChannelStr());
            channelIds.add(channelId);
        }
    }


    @Transactional
    public ResultVO addAIReplyTemplate(AIReplyTemplateAddReq req,Long userId){

        String replyContent = req.getReplyContent();

        AiReplyTemplate aiReplyTemplate = new AiReplyTemplate();

        aiReplyTemplate.setCreateTime(LocalDateTime.now());
        aiReplyTemplate.setUserId(userId);
        aiReplyTemplate.setReplyContent(replyContent);
        boolean saved = aiReplyTemplateService.save(aiReplyTemplate);
        if(!saved){
            return ResultVO.fail("保存回复模版失败！");
        }
        boolean updated = addFilterWords(req.getWordContents(), userId,aiReplyTemplate.getId(), req.getChannelIds());
        log.info("addAIReplyTemplate userId={},updated={} aiReplyTemplate={}",userId,updated, JSONObject.toJSONString(aiReplyTemplate));
        return ResultVO.success();
    }


    public boolean addFilterWords(List<String> wordContent, Long userId,Long replyId,List<Long> channelIds) {
        List<FilterWordInfo> filterWordInfos = new ArrayList<>();
        for (String content : wordContent) {
            FilterWordInfo filterWordInfo = new FilterWordInfo();
            filterWordInfo.setCreateUserId(userId);
            filterWordInfo.setCreateTime(LocalDateTime.now());
            filterWordInfo.setType(5);
            filterWordInfo.setStatus(1);
            filterWordInfo.setWordContent(content);
            filterWordInfo.setChannelStr(FilterWordServer.buildChannelIdsStr(channelIds));
            filterWordInfo.setReplyId(replyId);
            filterWordInfo.setUpdateUserId(userId);
            filterWordInfo.setUpdateTime(LocalDateTime.now());
            filterWordInfos.add(filterWordInfo);
        }
        return filterWordService.saveBatch(filterWordInfos);
    }



    @Transactional
    public ResultVO updateAIReplyTemplate(AIReplyTemplateUpdateReq req, Long userId){
        AiReplyTemplate template = aiReplyTemplateService.getById(req.getId());
        if(template==null || !Objects.equals(template.getUserId(), userId)){
            return ResultVO.fail("该模版不存在！");
        }
        // 将旧的删除
        template.setReplyContent(req.getReplyContent());

        aiReplyTemplateService.updateById(template);
        filterWordService.deleteReplyId(req.getId(),userId);
        addFilterWords(req.getWordContents(),userId,template.getId(),req.getChannelIds());
        return ResultVO.success();
    }


    @Transactional
    public ResultVO deleteAIReplyTemplate(Long id, Long userId){
        AiReplyTemplate template = aiReplyTemplateService.getById(id);
        if(template==null || !Objects.equals(template.getUserId(), userId)){
            return ResultVO.fail("该模版不存在！");
        }
        aiReplyTemplateService.removeById(id);
        filterWordService.deleteReplyId(id,userId);
        return ResultVO.success();
    }




    public AiReplyTemplateVo convertAiReplyTemplateVo(AiReplyTemplate aiReplyTemplate) {
        if ( aiReplyTemplate == null ) {
            return null;
        }

        AiReplyTemplateVo aiReplyTemplateVo = new AiReplyTemplateVo();

        aiReplyTemplateVo.setId( aiReplyTemplate.getId() );
        aiReplyTemplateVo.setReplyContent( aiReplyTemplate.getReplyContent() );
        aiReplyTemplateVo.setCreateTime( aiReplyTemplate.getCreateTime() );

        return aiReplyTemplateVo;
    }



}
