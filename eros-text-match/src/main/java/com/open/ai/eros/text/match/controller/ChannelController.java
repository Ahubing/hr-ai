package com.open.ai.eros.text.match.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.text.match.bean.ChannelSearchReq;
import com.open.ai.eros.text.match.bean.FilterWordChannelAddReq;
import com.open.ai.eros.text.match.bean.FilterWordChannelUpdateReq;
import com.open.ai.eros.text.match.bean.FilterWordChannelVo;
import com.open.ai.eros.text.match.config.TextMatchBaseController;
import com.open.ai.eros.text.match.manager.ChannelManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @类名：ChannelController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/17 16:29
 */

@Api(tags = "通道管理控制类")
@Slf4j
@RestController
public class ChannelController  extends TextMatchBaseController {


    @Autowired
    private ChannelManager channelManager;




    @ApiOperation("搜索通道")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @PostMapping("/channel/search")
    public ResultVO<PageVO<FilterWordChannelVo>> searchChannel(@RequestBody @Valid ChannelSearchReq req){
        return channelManager.searchChannel(req,getUserId());
    }



    /**
     * 新增通道
     * @param req
     * @return
     */
    @ApiOperation("新增通道")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @PostMapping("/channel/add")
    public ResultVO addChannel(@RequestBody @Valid FilterWordChannelAddReq req){

        return channelManager.addChannel(req,getUserId());
    }


    /**
     * 修改通道
     * @param req
     * @return
     */
    @ApiOperation("修改通道")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @PostMapping("/channel/update")
    public ResultVO updateChannel(@RequestBody @Valid FilterWordChannelUpdateReq req){

        return channelManager.updateChannel(req,getUserId());
    }


    /**
     * 删除通道
     * @return
     */
    @ApiOperation("删除通道")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @GetMapping("/channel/delete")
    public ResultVO deleteChannel(@RequestParam(value = "id") Long id){

        return channelManager.deleteChannelById(id,getUserId());
    }


    /**
     * 获取用户的通道
     * @return
     */
    @ApiOperation("获取用户的通道")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @GetMapping("/channel/list")
    public ResultVO channelList(){
        return channelManager.getChannelByUserId(getUserId());
    }



}
