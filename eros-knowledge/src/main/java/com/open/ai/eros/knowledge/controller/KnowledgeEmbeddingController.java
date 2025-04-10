package com.open.ai.eros.knowledge.controller;

import com.open.ai.eros.ai.bean.vo.EmbeddingSearchResultVo;
import com.open.ai.eros.ai.manager.EmbeddingSearchService;
import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.knowledge.config.KnowledgeBaseController;
import com.open.ai.eros.knowledge.manager.KnowledgeEmbeddingManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @类名：KnowledgeEmbeddingController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/12 19:49
 */

@Api(tags = "知识库切片向量化控制类")
@RestController
public class KnowledgeEmbeddingController  extends KnowledgeBaseController {


    @Autowired
    private KnowledgeEmbeddingManager knowledgeEmbeddingManager;


    @Autowired
    private EmbeddingSearchService embeddingSearchService;



    @ApiOperation("向量化知识库")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @GetMapping("/embedding/knowledge")
    public ResultVO embeddingKnowledge(@RequestParam(value = "knowledgeId") Long knowledge){
        return knowledgeEmbeddingManager.embeddingKnowledge(getUserId(),knowledge,getRole());
    }


    /**
     * 开始向量化文章
     *
     * @param docsId
     * @return
     */
    @ApiOperation("向量化文章")
    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @GetMapping("/embedding/docs")
    public ResultVO embeddingDocs(@RequestParam(value = "docsId") Long docsId){
        return knowledgeEmbeddingManager.embeddingDocs(getUserId(),docsId,getRole());
    }


    /**
     * 开始向量切片
     *
     * @param sliceId
     * @return
     */
    @ApiOperation("向量切片")
    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @GetMapping("/embedding/slice")
    public ResultVO embeddingSlice(@RequestParam(value = "sliceId") Long sliceId){
        return knowledgeEmbeddingManager.embeddingSlice(getUserId(),sliceId,getRole());
    }


    /**
     * 删除向量切片
     *
     * @param sliceId
     * @return
     */
    @ApiOperation("删除向量切片")
    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @GetMapping("/embedding/slice/delete")
    public ResultVO deleteEmbeddingSlice(@RequestParam(value = "sliceId") Long sliceId){
        return knowledgeEmbeddingManager.deleteEmbeddingSlice(getUserId(),sliceId,getRole());
    }



    @ApiOperation("知识库检索")
    @GetMapping("/embedding/knowledge/search")
    public ResultVO<EmbeddingSearchResultVo> searchKnowledgeSliceContent(@RequestParam(value = "knowledgeId",required = true) Long knowledgeId,
                                                                         @RequestParam(value = "text",required = true) String text){
        return embeddingSearchService.searchKnowledgeContentTest(knowledgeId,text);
    }



}
