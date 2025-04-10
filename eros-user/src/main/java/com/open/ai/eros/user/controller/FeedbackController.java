package com.open.ai.eros.user.controller;

import com.open.ai.eros.common.annotation.VerifyUserToken;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.user.bean.req.FeedbackSubmitReq;
import com.open.ai.eros.user.bean.vo.FeedbackVO;
import com.open.ai.eros.user.config.UserBaseController;
import com.open.ai.eros.user.manager.FeedbackManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @类名：FeedbackController
 * @项目名：web-eros-ai
 * @description：意见反馈--控制类
 * @创建人：陈臣
 * @创建时间：2024/8/6 19:57
 */
@Slf4j
@Api(tags = "意见反馈")
@RestController
public class FeedbackController extends UserBaseController {

    @Autowired
    private FeedbackManager feedbackManager;

    /**
     * 提交反馈
     * @param request
     * @return
     */
    @ApiOperation(value = "提交反馈")
    @VerifyUserToken
    @PostMapping("/feedback/submit")
    public ResultVO submitFeedback(@RequestBody @Valid FeedbackSubmitReq request) {
        return feedbackManager.submitFeedback(request, getUserId());
    }

    /**
     * 获取反馈列表
     * @param typeId 反馈类型ID
     * @param pageNum 页数
     * @param pageSize 每页数量
     * @return
     */
    @ApiOperation(value = "获取反馈列表")
    @VerifyUserToken
    @GetMapping("/feedback/list")
    public ResultVO<PageVO<FeedbackVO>> listFeedbacks(
            @RequestParam(required = false) Integer typeId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = getUserId();
        return feedbackManager.listFeedbacks(typeId, userId, pageNum, pageSize);
    }


    /**
     * 删除多个反馈
     * @param ids 反馈ID列表
     * @return
     */
    @ApiOperation(value = "删除多个反馈")
    @VerifyUserToken
    @DeleteMapping("/feedback/delete")
    public ResultVO deleteFeedbacks(@RequestParam("ids") List<Long> ids) {
        return feedbackManager.deleteFeedbacks(ids, getUserId());
    }



}
