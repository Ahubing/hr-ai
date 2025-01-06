package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.req.AddOrUpdateAmPromptReq;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.PromptManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

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
    private ResultVO promptList(@RequestParam(value = "type", required = false) Integer type) {
        Long adminId = getUserId();
        return promptManager.promptList(type, adminId);
    }

    @ApiOperation("获取AI跟进prompt详情")
    @VerifyUserToken
    @GetMapping("prompt/detail")
    private ResultVO promptDetail(@RequestParam(value = "id", required = true) Integer id) {
        return promptManager.promptDetail(id);
    }


    @ApiOperation("prompt新增或修改")
    @VerifyUserToken
    @PostMapping("prompt/edit")
    private ResultVO promptEdit(@RequestBody @Valid AddOrUpdateAmPromptReq req) {
        if (req == null) {
            return ResultVO.fail("参数不能为空");
        }
        req.setAdminId(getUserId());
        return promptManager.addOrUpdate(req);
    }

    @ApiOperation("删除prompt")
    @VerifyUserToken
    @GetMapping("prompt/delete")
    private ResultVO promptEdit(@RequestParam(value = "id", required = true) Integer id) {
        return promptManager.deleteById(id);
    }
}
