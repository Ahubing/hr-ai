package com.open.ai.eros.knowledge.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.knowledge.bean.vo.EmbedTestVo;
import com.open.ai.eros.knowledge.config.KnowledgeBaseController;
import com.open.ai.eros.knowledge.service.DemoVectorStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(tags = "向量测试demo控制类")
@RestController
@RequiredArgsConstructor
public class EmbeddingController extends KnowledgeBaseController {

    private final DemoVectorStoreService demoVectorStoreService;



    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @ApiOperation("文本向量化")
    @PostMapping("/embed")
    public ResultVO embed(@RequestBody EmbedTestVo embedTestVo) {
        String embedding = demoVectorStoreService.embedding(embedTestVo.getTemplateModel(), embedTestVo.getText());
        return ResultVO.success(embedding);
    }



    @VerifyUserToken(role = {RoleEnum.SYSTEM,RoleEnum.CREATOR})
    @ApiOperation("文本向量化+源数据")
    @PostMapping("/embed-meta")
    public ResultVO embedMeta(@RequestBody EmbedTestVo embedTestVo) {
        String embedding = demoVectorStoreService.embeddingWithMeta(embedTestVo.getTemplateModel(), embedTestVo.getText());
        return ResultVO.success(embedding);
    }




    @VerifyUserToken
    @ApiOperation("向量搜索")
    @GetMapping("/search")
    public ResultVO<List<String>> search(EmbedTestVo embedTestVo) {
        List<String> strings = demoVectorStoreService.search(embedTestVo.getTemplateModel(), embedTestVo.getText());
        return ResultVO.success(strings);
    }

}
