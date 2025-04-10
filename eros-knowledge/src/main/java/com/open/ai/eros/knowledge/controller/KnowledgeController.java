package com.open.ai.eros.knowledge.controller;


import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.knowledge.bean.req.KnowledgeAddReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeSearchReq;
import com.open.ai.eros.knowledge.bean.req.KnowledgeUpdateReq;
import com.open.ai.eros.knowledge.bean.vo.KnowledgeSimpleVo;
import com.open.ai.eros.knowledge.bean.vo.KnowledgeVo;
import com.open.ai.eros.knowledge.config.KnowledgeBaseController;
import com.open.ai.eros.knowledge.manager.KnowledgeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */

@Api(tags = "知识库管理类")
@RestController
public class KnowledgeController extends KnowledgeBaseController {


    @Autowired
    private KnowledgeManager knowledgeManager;


    @GetMapping("/simple/list")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("查询简单知识库信息")
    public ResultVO<List<KnowledgeSimpleVo>> simpleList(@RequestParam(value = "userId",required = false) Long userId){
        if(!RoleEnum.SYSTEM.getRole().equals(getRole())){
            userId = getUserId();
        }
        return knowledgeManager.simpleList(userId);
    }


    @GetMapping("/simple/c/list")
    @VerifyUserToken
    @ApiOperation("查询所有简单知识库信息")
    public ResultVO<List<KnowledgeSimpleVo>> simpleAllList(){
        return knowledgeManager.simpleList(null);
    }



    @GetMapping("/page")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("查询知识库")
    public ResultVO<PageVO<KnowledgeVo>> list(@Valid KnowledgeSearchReq req){
        if(!RoleEnum.SYSTEM.getRole().equals(getRole())){
            req.setUserId(getUserId());
        }
        return knowledgeManager.list(req);
    }


    @PostMapping("/add")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("新增知识库")
    public ResultVO  createKnowledge( @RequestBody @Valid KnowledgeAddReq req){

        return knowledgeManager.addKnowledge(getUserId(),req);
    }


    @PostMapping("/update")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("修改知识库")
    public ResultVO  updateKnowledge(@Valid @RequestBody KnowledgeUpdateReq req){

        return knowledgeManager.updateKnowledge(getUserId(),req,getRole());
    }


}

