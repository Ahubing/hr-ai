package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmPositionPost;
import com.open.ai.eros.db.mysql.hr.entity.AmPositionSection;
import com.open.ai.eros.db.mysql.hr.req.SearchPositionListReq;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.AmPositionSectionVo;
import com.open.ai.eros.db.mysql.hr.vo.AmPositionVo;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.ChatBotPositionManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2025/1/4 23:19
 */

@Api(tags = "ChatBot Position管理类")
@Slf4j
@RestController
public class ChatBotPositionController extends HrAIBaseController {

    @Resource
    private ChatBotPositionManager chatBotPositionManager;



    @ApiOperation("更新职位")
    @VerifyUserToken
    @PostMapping("position/update")
    public ResultVO updatePosition(@RequestBody @Valid updatePositionReq rēq) {
        if (Objects.isNull(rēq) ) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.updatePosition(rēq, getUserId());
    }


    @ApiOperation("生成岗位人才画像和岗位胜任力模型的评价标准和打分权重规则")
    @VerifyUserToken
    @GetMapping("position/competencyModel")
    public ResultVO competencyModel(@RequestParam(value = "id", required = true) Integer id) {

        return chatBotPositionManager.competencyModel(id, getUserId());
    }


    @ApiOperation("批量删除职位")
    @VerifyUserToken
    @DeleteMapping("position/batch_delete")
    public ResultVO positionBatchDelete(@RequestParam(value = "ids", required = true) List<String> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.batchDeletePosition(ids, getUserId());
    }


    @ApiOperation("批量关闭职位")
    @VerifyUserToken
    @DeleteMapping("position/batch_close")
    public ResultVO positionBatchClose(@RequestParam(value = "ids", required = true) List<Integer> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.batchClosePosition(ids, getUserId());

    }


    @ApiOperation("批量开放职位")
    @VerifyUserToken
    @DeleteMapping("position/batch_open")
    public ResultVO positionBatchOpen(@RequestParam(value = "ids", required = true) List<Integer> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.batchOpenPosition(ids, getUserId());

    }

    @ApiOperation("获取组织架构")
    @VerifyUserToken
    @GetMapping("position/get_structures")
    public ResultVO<List<AmPositionSectionVo>> getStructures(@RequestParam(value = "name", required = false) @ApiParam("部门或岗位名称") String name) {
        return chatBotPositionManager.getStructures(getUserId(),name);
    }


    @ApiOperation("招聘人员跟进职位")
    @VerifyUserToken
    @PostMapping("position/bind_uid")
    public ResultVO positionBindUid(@RequestBody @Valid BindPositionUidReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.positionBindUid(req);
    }


    @ApiOperation("职位绑定AI助手")
    @VerifyUserToken
    @PostMapping("position/bind_ai_assistant")
    public ResultVO bindAiAssistant(@RequestBody @Valid BindAiAssistantReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.bindAiAssistant(req);
    }


    @ApiOperation("职位关联岗位")
    @VerifyUserToken
    @PostMapping("position/bind_post")
    public ResultVO bindPost(@RequestBody @Valid BindPositionPostReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.bindPost(req);
    }


    @ApiOperation("新增/编辑-岗位")
    @VerifyUserToken
    @PostMapping("position/save_post")
    public ResultVO savePost(@RequestBody @Valid AddPositionPostReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.savePost(req);
    }


    @ApiOperation("查询岗位列表")
    @VerifyUserToken
    @GetMapping("position/get_post_list")
    public ResultVO<List<AmPositionPost>> getPostList(@RequestParam(value = "sectionId", required = true) Integer sectionId) {
        return chatBotPositionManager.getPostList(sectionId);
    }


    @ApiOperation("查询部门列表")
    @VerifyUserToken
    @GetMapping("position/get_section_list")
    public ResultVO<List<AmPositionSection>> getSectionList(@RequestParam(value = "deptName",required = false) @ApiParam("部门名称") String deptName) {
        Long userId = getUserId();
        return chatBotPositionManager.getSectionList(userId,deptName);
    }

    @ApiOperation("新增/编辑部门")
    @VerifyUserToken
    @PostMapping("position/edit_section")
    public ResultVO editSection(@RequestBody @Valid AddOrUpdateSectionReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }

        return chatBotPositionManager.editSection(req, getUserId());
    }

    @ApiOperation("查询职位详情")
    @VerifyUserToken
    @GetMapping("position/detail")
    public ResultVO<AmPositionVo> getPositionDetail(@RequestParam(value = "id", required = true) Integer id) {
        return chatBotPositionManager.getPositionDetail(id);
    }


    @ApiOperation("获取职位列表")
    @VerifyUserToken
    @PostMapping("position/list")
    public ResultVO<PageVO<AmPositionVo>> getPositionList(@RequestBody @Valid SearchPositionListReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.getPositionList(req, getUserId());
    }


}
