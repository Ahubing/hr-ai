package com.open.ai.eros.text.match.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.text.match.bean.AIReplyTemplateAddReq;
import com.open.ai.eros.text.match.bean.AIReplyTemplateSearchReq;
import com.open.ai.eros.text.match.bean.AIReplyTemplateUpdateReq;
import com.open.ai.eros.text.match.bean.AiReplyTemplateVo;
import com.open.ai.eros.text.match.config.TextMatchBaseController;
import com.open.ai.eros.text.match.manager.AIReplyTemplateManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @类名：AIReplyTemplateController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/20 1:12
 */

@Api(tags = "ai回复模版控制类")
@RestController
@Slf4j
public class AIReplyTemplateController extends TextMatchBaseController {



    @Autowired
    private AIReplyTemplateManager aiReplyTemplateManager;


    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @GetMapping("reply/template/bind/channel")
    public ResultVO bindChannel(@RequestParam("id") Long id,@RequestParam("channelId") Long channelId){

        return aiReplyTemplateManager.bindChannel(id,channelId,getUserId());
    }



    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("新增回复模版")
    @PostMapping("/reply/template/add")
    public ResultVO addAIReplyTemplate(@RequestBody @Valid AIReplyTemplateAddReq req){

        return aiReplyTemplateManager.addAIReplyTemplate(req,getUserId());
    }



    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("修改回复模版")
    @PostMapping("/reply/template/update")
    public ResultVO updateAIReplyTemplate(@RequestBody @Valid AIReplyTemplateUpdateReq req){

        return aiReplyTemplateManager.updateAIReplyTemplate(req,getUserId());
    }




    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("删除回复模版")
    @GetMapping("/reply/template/delete")
    public ResultVO deleteAIReplyTemplate(@RequestParam(value = "id") Long id){

        return aiReplyTemplateManager.deleteAIReplyTemplate(id,getUserId());
    }


    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("搜索回复模版")
    @GetMapping("/reply/template/search")
    public ResultVO<PageVO<AiReplyTemplateVo>> search(@Valid AIReplyTemplateSearchReq req){
        return aiReplyTemplateManager.search(req,getUserId());
    }




}
