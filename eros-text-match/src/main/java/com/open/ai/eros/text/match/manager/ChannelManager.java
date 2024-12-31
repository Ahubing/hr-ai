package com.open.ai.eros.text.match.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.creator.service.impl.MaskServiceImpl;
import com.open.ai.eros.db.mysql.text.entity.FilterWordChannelInfo;
import com.open.ai.eros.db.mysql.text.service.impl.FilterWordChannelInfoServiceImpl;
import com.open.ai.eros.text.match.bean.ChannelSearchReq;
import com.open.ai.eros.text.match.bean.FilterWordChannelAddReq;
import com.open.ai.eros.text.match.bean.FilterWordChannelUpdateReq;
import com.open.ai.eros.text.match.bean.FilterWordChannelVo;
import com.open.ai.eros.text.match.convert.ChannelConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @类名：ChannelManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/20 0:24
 */
@Slf4j
@Component
public class ChannelManager {


    @Autowired
    private FilterWordChannelInfoServiceImpl filterWordChannelInfoService;

    @Autowired
    private MaskServiceImpl maskService;



    public ResultVO<List<FilterWordChannelVo>> getChannelByUserId(Long userId){
        LambdaQueryWrapper<FilterWordChannelInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FilterWordChannelInfo::getStatus,1);
        lambdaQueryWrapper.eq(FilterWordChannelInfo::getCreateUserId,userId);
        List<FilterWordChannelInfo> wordChannelInfos = filterWordChannelInfoService.list(lambdaQueryWrapper);
        List<FilterWordChannelVo> filterWordChannelVos = wordChannelInfos.stream().map(this::convertFilterWordChannelVo).collect(Collectors.toList());
        return ResultVO.success(filterWordChannelVos);
    }



    public ResultVO<PageVO<FilterWordChannelVo>> searchChannel(ChannelSearchReq req,Long userId){

        LambdaQueryWrapper<FilterWordChannelInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Page<FilterWordChannelInfo> page = new Page<>(req.getPage(),req.getPageSize());
        if(StringUtils.isNoneEmpty(req.getKeyWord())){
            lambdaQueryWrapper.like(FilterWordChannelInfo::getChannelName,"%"+req.getKeyWord()+"%");
        }
        lambdaQueryWrapper.eq(FilterWordChannelInfo::getCreateUserId,userId);

        Page<FilterWordChannelInfo> infoPage = filterWordChannelInfoService.page(page, lambdaQueryWrapper);

        List<FilterWordChannelVo> filterWordChannelVos = infoPage.getRecords().stream().map(this::convertFilterWordChannelVo).collect(Collectors.toList());
        return ResultVO.success(PageVO.build(infoPage.getTotal(),filterWordChannelVos));
    }

    @Transactional
    public ResultVO deleteChannelById(Long id,Long userId){
        FilterWordChannelInfo channelInfo = filterWordChannelInfoService.getById(id);
        if(channelInfo==null || !Objects.equals(channelInfo.getCreateUserId(), userId)){
            return ResultVO.fail("通道不存在！");
        }

        boolean removed = filterWordChannelInfoService.removeById(id);
        int updated = maskService.updateMaskChannelNull(id, userId);
        log.info("deleteChannelById id={} removed={},updated={} ",id,removed,updated);
        return ResultVO.success();
    }


    public ResultVO updateChannel(FilterWordChannelUpdateReq req,Long userId){

        FilterWordChannelInfo channelInfo = filterWordChannelInfoService.getById(req.getId());
        if(channelInfo==null || !Objects.equals(channelInfo.getCreateUserId(), userId)){
            return ResultVO.fail("通道不存在！");
        }
        FilterWordChannelInfo info = convertFilterWordChannelInfo(req);

        boolean updated = filterWordChannelInfoService.updateById(info);
        log.info("updateChannel  updated={} , channelId={}",updated,req.getId());
        return updated?ResultVO.success():ResultVO.fail("更新失败！");
    }




    public ResultVO addChannel(FilterWordChannelAddReq req, Long userId){
        FilterWordChannelInfo info = convertFilterWordChannelInfo(req);
        info.setCreateTime(LocalDateTime.now());
        info.setCreateUserId(userId);
        boolean saved = filterWordChannelInfoService.save(info);
        log.info("addChannel  updated={} , userId={}",saved,userId);
        return saved?ResultVO.success():ResultVO.fail("更新失败！");
    }


    public FilterWordChannelVo convertFilterWordChannelVo(FilterWordChannelInfo info) {
        if ( info == null ) {
            return null;
        }

        FilterWordChannelVo filterWordChannelVo = new FilterWordChannelVo();

        filterWordChannelVo.setId( info.getId() );
        filterWordChannelVo.setType( info.getType() );
        filterWordChannelVo.setChannelName( info.getChannelName() );
        filterWordChannelVo.setRemark( info.getRemark() );
        filterWordChannelVo.setStatus( info.getStatus() );
        filterWordChannelVo.setCreateTime( info.getCreateTime() );

        return filterWordChannelVo;
    }


    public FilterWordChannelInfo convertFilterWordChannelInfo(FilterWordChannelAddReq req) {
        if ( req == null ) {
            return null;
        }

        FilterWordChannelInfo filterWordChannelInfo = new FilterWordChannelInfo();

        filterWordChannelInfo.setType( req.getType() );
        filterWordChannelInfo.setChannelName( req.getChannelName() );
        filterWordChannelInfo.setRemark( req.getRemark() );
        filterWordChannelInfo.setStatus( req.getStatus() );

        return filterWordChannelInfo;
    }

    public FilterWordChannelInfo convertFilterWordChannelInfo(FilterWordChannelUpdateReq req) {
        if ( req == null ) {
            return null;
        }

        FilterWordChannelInfo filterWordChannelInfo = new FilterWordChannelInfo();

        filterWordChannelInfo.setId( req.getId() );
        filterWordChannelInfo.setType( req.getType() );
        filterWordChannelInfo.setChannelName( req.getChannelName() );
        filterWordChannelInfo.setRemark( req.getRemark() );
        filterWordChannelInfo.setStatus( req.getStatus() );

        return filterWordChannelInfo;
    }


}
