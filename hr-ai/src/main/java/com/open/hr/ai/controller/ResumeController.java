package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.hr.ai.bean.req.SearchAmResumeReq;
import com.open.hr.ai.bean.vo.AmResumeCountDataVo;
import com.open.hr.ai.bean.vo.AmResumeVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.ResumeManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2025/1/4 23:19
 */

@Api(tags = "resume管理类")
@Slf4j
@RestController
public class ResumeController extends HrAIBaseController {


    @Resource
    private ResumeManager resumeManager;


    @ApiOperation("获取简历列表")
    @VerifyUserToken
    @GetMapping("resume/list")
    public ResultVO<PageVO<AmResume>> promptList(@RequestParam(value = "type", required = true) Integer type, @RequestParam(value = "post_id", required = false) Integer post_id, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "page", required = true) Integer page, @RequestParam(value = "size", required = true) Integer size) {
        return resumeManager.resumeList(getUserId(), type, post_id, name, page, size);
    }

    @ApiOperation("统计简历数据")
    @VerifyUserToken
    @GetMapping("resume/data")
    public ResultVO<List<AmResumeCountDataVo>> promptData() {
        return resumeManager.resumeData(getUserId());
    }

    @ApiOperation("获取简历详情")
    @VerifyUserToken
    @GetMapping("resume/detail")
    public ResultVO<AmResumeVo> promptDetail(@RequestParam(value = "id", required = true) Integer id) {
        return resumeManager.resumeDetail(id);
    }


    /**
     * todo 待补充php 高级的智能匹配...(php没写)
     *
     * @param searchAmResumeReq
     * @return
     */
    @ApiOperation("智能匹配")
    @VerifyUserToken
    @PostMapping("resume/search")
    public ResultVO resumeSearch(@RequestBody @Valid SearchAmResumeReq searchAmResumeReq) {
        if (Objects.isNull(searchAmResumeReq)) {
            return ResultVO.fail("参数不能为空");
        }

       return resumeManager.resumeSearch(searchAmResumeReq,getUserId());
    }


}
