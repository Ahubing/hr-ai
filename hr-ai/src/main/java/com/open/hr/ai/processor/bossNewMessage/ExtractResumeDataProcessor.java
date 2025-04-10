package com.open.hr.ai.processor.bossNewMessage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmChatMessage;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatMessageServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmPositionServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmResumeServiceImpl;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import com.open.hr.ai.util.AmClientTaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提取简历信息
 */
@Order(1)
@Component
@Slf4j
public class ExtractResumeDataProcessor implements BossNewMessageProcessor {

    @Resource
    private AmResumeServiceImpl amResumeService;

    @Resource
    private AmPositionServiceImpl amPositionService;
    @Resource
    private AmChatMessageServiceImpl amChatMessageService;
    @Resource
    private AmClientTaskUtil amClientTaskUtil;

    /**
     * 根据聊天内容,用来提取用户手机和微信号
     */
    @Override
    public ResultVO dealBossNewMessage(AtomicInteger statusCode, String platform, AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {
        log.info("ExtractResumeDataProcessor dealBossNewMessage amResume={},amZpLocalAccouts={},req={}", amResume, amZpLocalAccouts, req);
        JSONObject chatInfo = req.getChat_info();
        if (Objects.isNull(chatInfo) || chatInfo.isEmpty()) {
            log.error("extractData chatData is null,bossId={}", amZpLocalAccouts.getId());
            return ResultVO.fail("chatData is null");
        }

        String userId = req.getUser_id();
        // 保存用户信息
        LambdaQueryWrapper<AmResume> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmResume::getUid, userId);
        queryWrapper.eq(AmResume::getAccountId, amZpLocalAccouts.getId());
        AmResume innerAmResume = amResumeService.getOne(queryWrapper, false);
//        log.info("ExtractResumeDataProcessor dealBossNewMessage innerAmResume={}", innerAmResume);
        if (Objects.nonNull(innerAmResume)) {
            BeanUtils.copyProperties(innerAmResume, amResume);
            if (Objects.nonNull(chatInfo.get("phone"))) {
                amResume.setPhone(chatInfo.get("phone").toString());
            }
            if (Objects.nonNull(chatInfo.get("weixin"))) {
                amResume.setWechat(chatInfo.get("weixin").toString());
            }
            if (Objects.nonNull(chatInfo.get("name"))) {
                amResume.setName(chatInfo.get("name").toString());
            }
            if (Objects.nonNull(chatInfo.get("type"))) {
                ReviewStatusEnums statusEnums = ReviewStatusEnums.getEnumByStatus(Integer.parseInt(chatInfo.get("type").toString()));
                amResumeService.updateType(amResume, false, statusEnums,false);
            }
            if (CollectionUtils.isNotEmpty(req.getAttachment_resume())) {
                amResume.setAttachmentResume(JSONObject.toJSONString(req.getAttachment_resume()));
            }
            if (Objects.nonNull(chatInfo.get("toPositionId"))) {
                String toPositionId = chatInfo.get("toPositionId").toString();
                LambdaQueryWrapper<AmPosition> positionQueryWrapper = new LambdaQueryWrapper<>();
                positionQueryWrapper.eq(AmPosition::getEncryptId, toPositionId);
                positionQueryWrapper.eq(AmPosition::getBossId, amZpLocalAccouts.getId());
                AmPosition amPositionServiceOne = amPositionService.getOne(positionQueryWrapper, false);
                if (Objects.nonNull(amPositionServiceOne)) {
                    // 通过chat_info检测到职位变动要重新request_info在线简历，并清空聊天记录。然后再去发送消息
                    if (!Objects.equals(amPositionServiceOne.getId(), amResume.getPostId())) {
                        amResumeService.updateType(amResume, false, ReviewStatusEnums.ABANDON,false);
                        // 重新发起请求
                        amClientTaskUtil.buildRequestTask(amZpLocalAccouts, Integer.parseInt(amResume.getUid()), amResume, false);

                        // 清空聊天记录
                        LambdaQueryWrapper<AmChatMessage> amChatMessageLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        String conversationId = amZpLocalAccouts.getId() + "_" + amResume.getUid();
                        amChatMessageLambdaQueryWrapper.eq(AmChatMessage::getConversationId, conversationId);
                        boolean remove = amChatMessageService.remove(amChatMessageLambdaQueryWrapper);
                        log.info("dealUserAllInfoData remove amChatMessage result={},conversationId={}", remove, conversationId);
                        amResume.setType(ReviewStatusEnums.BUSINESS_SCREENING.getStatus());
                    }
                    amResume.setPostId(amPositionServiceOne.getId());
                }
            }
            boolean result = amResumeService.updateById(amResume);
            log.info("ExtractResumeDataProcessor dealBossNewMessage update amResume result={}", result);
        }
        else {
            if (Objects.nonNull(chatInfo.get("encryptUid"))) {
                amResume.setEncryptGeekId(chatInfo.get("encryptUid").toString());
            }
            if (Objects.nonNull(chatInfo.get("name"))) {
                amResume.setName(chatInfo.get("name").toString());
            }
            if (CollectionUtils.isNotEmpty(req.getAttachment_resume())) {
                amResume.setAttachmentResume(JSONObject.toJSONString(req.getAttachment_resume()));
            }
            amResume.setUid(userId);
            amResume.setAdminId(amZpLocalAccouts.getAdminId());
            amResume.setAccountId(amZpLocalAccouts.getId());
            amResumeService.updateType(amResume, false, ReviewStatusEnums.BUSINESS_SCREENING,false);
            if (Objects.nonNull(chatInfo.get("toPositionId"))) {
                String toPositionId = chatInfo.get("toPositionId").toString();
                LambdaQueryWrapper<AmPosition> positionQueryWrapper = new LambdaQueryWrapper<>();
                positionQueryWrapper.eq(AmPosition::getEncryptId, toPositionId);
                positionQueryWrapper.eq(AmPosition::getBossId, amZpLocalAccouts.getId());
                AmPosition amPositionServiceOne = amPositionService.getOne(positionQueryWrapper, false);
                if (Objects.nonNull(amPositionServiceOne)) {
                    amResume.setPostId(amPositionServiceOne.getId());
                }
            }

            boolean result = amResumeService.save(amResume);
            log.info("ExtractResumeDataProcessor dealBossNewMessage save amResume={} result={}", amResume, result);
        }
        return ResultVO.success();
    }


}
