package com.open.ai.eros.user.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.user.entity.Feedback;
import com.open.ai.eros.db.mysql.user.service.impl.FeedbackServiceImpl;
import com.open.ai.eros.user.bean.req.FeedbackSubmitReq;
import com.open.ai.eros.user.bean.vo.FeedbackVO;
import com.open.ai.eros.user.convert.FeedbackConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 意见反馈---业务类
 */
@Slf4j
@Component
public class FeedbackManager {

    @Autowired
    private FeedbackServiceImpl feedbackService;

    /**
     * 提交反馈
     * @param request 反馈提交请求对象
     * @param userId 用户ID
     * @return 提交结果
     */
    public ResultVO submitFeedback(FeedbackSubmitReq request, Long userId) {
        Feedback feedback = FeedbackConvert.I.convertFeedback(request);
        feedback.setUserId(userId);
        feedback.setCreateTime(LocalDateTime.now());
        feedback.setModifyTime(LocalDateTime.now());

        boolean success = feedbackService.save(feedback);
        return success ? ResultVO.success("反馈提交成功") : ResultVO.fail("反馈提交失败");
    }

    /**
     * 获取反馈列表
     * @param typeId 反馈类型ID（可选）
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页的反馈列表
     */
    public ResultVO<PageVO<FeedbackVO>> listFeedbacks(Integer typeId, Long userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<Feedback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Feedback::getUserId, userId);
        if (typeId != null) {
            queryWrapper.eq(Feedback::getTypeId, typeId);
        }
        queryWrapper.orderByDesc(Feedback::getCreateTime);

        Page<Feedback> pageObj = new Page<>(pageNum, pageSize);
        Page<Feedback> page = feedbackService.page(pageObj, queryWrapper);

        List<FeedbackVO> feedbackVOs = page.getRecords().stream()
                .map(FeedbackConvert.I::convertFeedbackVO)
                .collect(Collectors.toList());

        return ResultVO.success(PageVO.build(page.getTotal(), feedbackVOs));
    }


    /**
     * 删除多个反馈
     * @param ids 反馈ID列表
     * @param userId 用户ID
     * @return 删除结果
     */
    public ResultVO deleteFeedbacks(List<Long> ids, Long userId) {
        if (ids == null || ids.isEmpty()) {
            return ResultVO.fail("请选择要删除的反馈");
        }

        List<Feedback> feedbacks = feedbackService.listByIds(ids);
        List<Long> validIds = new ArrayList<>();

        for (Feedback feedback : feedbacks) {
            if (feedback.getUserId().equals(userId)) {
                validIds.add(feedback.getId());
            }
        }

        if (validIds.isEmpty()) {
            return ResultVO.fail("没有可删除的反馈或无权限删除");
        }

        boolean success = feedbackService.removeByIds(validIds);

        if (success) {
            if (validIds.size() == ids.size()) {
                return ResultVO.success("所有选中的反馈删除成功");
            } else {
                return ResultVO.success("部分反馈删除成功，" + (ids.size() - validIds.size()) + "条反馈无权限删除");
            }
        } else {
            return ResultVO.fail("反馈删除失败");
        }
    }


}
