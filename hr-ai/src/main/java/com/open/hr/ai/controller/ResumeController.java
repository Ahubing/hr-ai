package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.req.AddOrUpdateAmPromptReq;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.PromptManager;
import com.open.hr.ai.manager.ResumeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Date 2025/1/4 23:19
 */

@Api(tags = "resume管理类")
@Slf4j
@RestController
public class ResumeController extends HrAIBaseController {


    @Resource
    private ResumeManager resumeManager;



    @ApiOperation("获取简历详情")
    @VerifyUserToken
    @GetMapping("resume/detail")
    private ResultVO promptDetail(@RequestParam(value = "id", required = true) Integer id) {
        return resumeManager.resumeDetail(id);
    }




    /**
     * todo 待补充php 高级的智能匹配...(php没写)
     * @param id
     * @return
     */
    @ApiOperation("智能匹配")
    @VerifyUserToken
    @GetMapping("resume/search")
    private ResultVO resumeSearch(@RequestParam(value = "id", required = true) Integer id) {
        return  ResultVO.success("智能匹配");
    }


}
