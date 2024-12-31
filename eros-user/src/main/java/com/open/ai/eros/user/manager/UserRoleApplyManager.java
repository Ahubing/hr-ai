package com.open.ai.eros.user.manager;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.constants.RoleEnum;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.RoleApplyStatusEnum;
import com.open.ai.eros.db.mysql.user.entity.User;
import com.open.ai.eros.db.mysql.user.entity.UserRoleApply;
import com.open.ai.eros.db.mysql.user.service.impl.UserRoleApplyServiceImpl;
import com.open.ai.eros.db.mysql.user.service.impl.UserServiceImpl;
import com.open.ai.eros.social.bean.req.PushMessageAddReq;
import com.open.ai.eros.social.email.manager.MailServiceManager;
import com.open.ai.eros.social.email.manager.ThreadPoolManager;
import com.open.ai.eros.social.manager.PushMessageManager;
import com.open.ai.eros.user.bean.req.UserApplyCreatorReq;
import com.open.ai.eros.user.convert.UserRoleApplyConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class UserRoleApplyManager {


    @Autowired
    private UserRoleApplyServiceImpl userRoleApplyService;


    @Autowired
    private UserServiceImpl userService;


    @Autowired
    private PushMessageManager pushMessageManager;


    @Autowired
    private MailServiceManager mailServiceManager;


    @Value("${project.name}")
    private String projectName;


    /**
     * 申请
     *
     * @return
     */
    public ResultVO applyCreatorRole(Long userId, UserApplyCreatorReq req) {

        LambdaQueryWrapper<UserRoleApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRoleApply::getUserId, userId);
        queryWrapper.eq(UserRoleApply::getStatus, RoleApplyStatusEnum.IN_PROGRESS.name());
        UserRoleApply userRoleApplying = userRoleApplyService.getOne(queryWrapper);
        if (userRoleApplying == null) {
            return ResultVO.fail("已经申请过了，正在审批中！");
        }
        UserRoleApply userRoleApply = UserRoleApplyConvert.I.convertUserRoleApply(req);
        userRoleApply.setCreatedAt(LocalDateTime.now());
        userRoleApply.setStatus(RoleApplyStatusEnum.IN_PROGRESS.name());
        userRoleApply.setUserId(userId);
        userRoleApply.setRequestedRole(RoleEnum.CREATOR.getRole());
        boolean saveResult = userRoleApplyService.save(userRoleApply);
        log.info("applyCreatorRole saveResult={},userRoleApply={}", saveResult, JSONObject.toJSONString(userRoleApply));
        return saveResult ? ResultVO.success() : ResultVO.fail("申请失败，请联系管理员！");
    }


    /**
     * 审批创作者
     *
     * @param opAccount 管理员用户id
     * @param userId   用户id
     * @return
     */
    @Transactional
    public ResultVO approvalCreator(String opAccount, Long userId) {

        LambdaQueryWrapper<UserRoleApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRoleApply::getUserId, userId);
        queryWrapper.eq(UserRoleApply::getStatus, RoleApplyStatusEnum.IN_PROGRESS.name());
        UserRoleApply userRoleApply = userRoleApplyService.getOne(queryWrapper);
        if (userRoleApply == null) {
            return ResultVO.fail("没有该用户申请需要审批！");
        }

        User user = userService.getById(userId);
        if(user==null){
            return ResultVO.fail("该用户不存在！");
        }

        userRoleApply.setStatus(RoleApplyStatusEnum.PROCESSED.name());
        boolean updateById = userRoleApplyService.updateById(userRoleApply);
        if (updateById) {
            User updateUser = new User();
            updateUser.setId(userId);
            updateUser.setRole(RoleEnum.CREATOR.getRole());
            boolean updateUserResult = userService.updateById(updateUser);
            log.info("approvalCreator opAccount={} user={} updateUserResult={}", opAccount, JSONObject.toJSONString(updateUser), updateUserResult);
            if (!updateUserResult) {
                throw new BizException("更新用户创作者权限失败！");
            }
        }
        //推送消息给用户  1. 邮件 2. 站内信
        sendMessage(opAccount, user.getEmail(), userId);
        return ResultVO.success();
    }


    public void sendMessage(String opUser,String email,Long userId){
        String title = "来自" + projectName + "一份通知信息";
        String content = "创作者权限审批成功！";
        ThreadPoolManager.sendEmailPool.execute(()->{
            PushMessageAddReq pushMessageAddReq = new PushMessageAddReq();
            pushMessageAddReq.setContent(content);
            pushMessageAddReq.setPushTo("C");
            pushMessageAddReq.setTitle(title);
            pushMessageAddReq.setTargetUserId(userId);
            pushMessageManager.addMessage(opUser, pushMessageAddReq);
        });
        mailServiceManager.sendTextMail(email,title,content);
    }


}
