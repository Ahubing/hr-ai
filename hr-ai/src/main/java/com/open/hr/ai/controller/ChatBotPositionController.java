package com.open.hr.ai.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.config.HrAIBaseController;
import com.open.hr.ai.manager.ChatBotOptionsManager;
import com.open.hr.ai.manager.ChatBotPositionManager;
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

@Api(tags = "ChatBot Position管理类")
@Slf4j
@RestController
public class ChatBotPositionController extends HrAIBaseController {

    @Resource
    private ChatBotPositionManager chatBotPositionManager;

    @ApiOperation("批量删除职位")
    @VerifyUserToken
    @DeleteMapping("position/batch_delete")
    public ResultVO positionBatchDelete(@RequestParam(value = "ids",required = true) List<String> ids) {
            if (Objects.isNull(ids) || ids.isEmpty()) {
                return ResultVO.fail("参数不能为空");
            }
            return chatBotPositionManager.batchDeletePosition(ids,getUserId());
    }


    @ApiOperation("批量关闭职位")
    @VerifyUserToken
    @DeleteMapping("position/batch_close")
    public ResultVO positionBatchClose(@RequestParam(value = "ids",required = true) List<String> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.batchClosePosition(ids,getUserId());

    }


    @ApiOperation("批量开放职位")
    @VerifyUserToken
    @DeleteMapping("position/batch_open")
    public ResultVO positionBatchOpen(@RequestParam(value = "ids",required = true) List<String> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.batchOpenPosition(ids,getUserId());

    }

    @ApiOperation("获取组织架构")
    @VerifyUserToken
    @GetMapping("position/get_structures")
    public ResultVO getStructures() {
        return chatBotPositionManager.getStructures(getUserId());
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
    public ResultVO savePost(@RequestBody @Valid AddPositionReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        return chatBotPositionManager.savePost(req);
    }


    @ApiOperation("查询岗位列表")
    @VerifyUserToken
    @GetMapping("position/get_post_list")
    public ResultVO getPostList(@RequestParam(value = "sectionId",required = true) Integer sectionId) {
        return chatBotPositionManager.getPostList(sectionId);
    }


    @ApiOperation("查询部门列表")
    @VerifyUserToken
    @GetMapping("position/get_section_list")
    public ResultVO getSectionList() {
        Long userId = getUserId();
        return chatBotPositionManager.getSectionList(userId);
    }

    @ApiOperation("新增/编辑部门")
    @VerifyUserToken
    @PostMapping("position/edit_section")
    public ResultVO editSection(@RequestBody @Valid AddOrUpdateSectionReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        req.setAdminId(getUserId());
        return chatBotPositionManager.editSection(req);
    }

    @ApiOperation("查询职位详情")
    @VerifyUserToken
    @GetMapping("position/detail")
    public ResultVO getPositionDetail(@RequestParam(value = "id",required = true) Integer id) {
        return chatBotPositionManager.getPositionDetail(id);
    }



    @ApiOperation("获取职位列表")
    @VerifyUserToken
    @PostMapping("position/list")
    public ResultVO getPositionList(@RequestBody @Valid SearchPositionListReq req) {
        if (Objects.isNull(req)) {
            return ResultVO.fail("参数不能为空");
        }
        req.setAdminId(getUserId());
        return chatBotPositionManager.getPositionList(req);
    }


}
