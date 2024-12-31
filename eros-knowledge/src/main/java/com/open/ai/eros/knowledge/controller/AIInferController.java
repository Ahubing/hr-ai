package com.open.ai.eros.knowledge.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.knowledge.config.KnowledgeBaseController;
import com.open.ai.eros.knowledge.manager.DocsSliceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @类名：InferQuestionContoller
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/16 12:50
 */

@Api(tags = "问题推理控制类")
@RestController
public class AIInferController extends KnowledgeBaseController {

    @Autowired
    private DocsSliceManager docsSliceManager;


    @GetMapping("/infer/slice/question")
    @VerifyUserToken(role = {RoleEnum.CREATOR,RoleEnum.SYSTEM})
    @ApiOperation("切片的推理问题")
    public ResultVO<List<String>> getInferQuestion(@RequestParam(value = "id") Long id){
        return docsSliceManager.getInferQuestion(getUserId(),id);
    }



    @GetMapping("/infer/question/text")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @ApiOperation("文本的推理问题")
    public ResultVO<List<String>> getInferQuestion(@RequestParam(value = "text") String text){
        return docsSliceManager.getInferQuestion(text);
    }



    @GetMapping("/infer/question/title")
    @VerifyUserToken(role = {RoleEnum.SYSTEM})
    @ApiOperation("文本的推理标题")
    public ResultVO<List<String>> getInferTitle(@RequestParam(value = "title") String title){
        return docsSliceManager.getInferTitle(title);
    }



}
