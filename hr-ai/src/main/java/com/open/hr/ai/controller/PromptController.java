package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmPrompt;
import com.open.hr.ai.bean.req.AddOrUpdateAmPromptReq;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.PromptManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Date 2025/1/4 23:19
 */

@Api(tags = "Prompt管理类")
@Slf4j
@RestController
public class PromptController extends HrAIBaseController {


    @Resource
    private PromptManager promptManager;

    @ApiOperation("获取AI跟进prompt列表")
    @VerifyUserToken
    @GetMapping("prompt/list")
    public ResultVO<List<AmPrompt>> getPromptList(@RequestParam(value = "type", required = false) Integer type) {
        Long adminId = getUserId();
        return promptManager.getPromptList(type, adminId);
    }

    @ApiOperation("获取AI跟进prompt详情")
    @VerifyUserToken
    @GetMapping("prompt/detail")
    public ResultVO<AmPrompt>  getPromptDetail(@RequestParam(value = "id", required = true) Integer id) {
        return promptManager.getPromptDetail(id);
    }


    @ApiOperation("prompt新增或修改")
    @VerifyUserToken
    @PostMapping("prompt/edit")
    public ResultVO addOrUpdatePrompt(@RequestBody @Valid AddOrUpdateAmPromptReq req) {
        if (req == null) {
            return ResultVO.fail("参数不能为空");
        }
        return promptManager.addOrUpdatePrompt(req,getUserId());
    }

    @ApiOperation("删除prompt")
    @VerifyUserToken
    @GetMapping("prompt/delete")
    public ResultVO deletePromptById(@RequestParam(value = "id", required = true) Integer id) {
        return promptManager.deletePromptById(id);
    }
}
