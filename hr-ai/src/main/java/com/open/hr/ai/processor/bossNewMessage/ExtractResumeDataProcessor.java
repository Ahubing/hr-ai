package com.open.hr.ai.processor.bossNewMessage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.service.impl.AmPositionServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmResumeServiceImpl;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 用于分析当前用户的prompt
 */
@Order(1)
@Component
@Slf4j
public class ExtractResumeDataProcessor implements BossNewMessageProcessor {

    @Resource
    private AmResumeServiceImpl amResumeService;

    @Resource
    private AmPositionServiceImpl amPositionService;

    /**
     * 根据聊天内容,用来提取用户手机和微信号
     */
    @Override
    public ResultVO dealBossNewMessage(String platform,AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {
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
        AmResume innerAmResume = amResumeService.getOne(queryWrapper, false);
        log.info("ExtractResumeDataProcessor dealBossNewMessage innerAmResume={}", innerAmResume);
        if (Objects.nonNull(innerAmResume)) {
            BeanUtils.copyProperties(innerAmResume, amResume);
            if (Objects.isNull(chatInfo.get("phone")) && Objects.isNull(chatInfo.get("weixin"))) {
                log.error("extractData phone and weixin is null,bossId={}", amZpLocalAccouts.getId());
                return ResultVO.fail("phone and weixin is null");
            }
            if (Objects.nonNull(chatInfo.get("phone"))) {
                amResume.setPhone(chatInfo.get("phone").toString());
            }
            if (Objects.nonNull(chatInfo.get("weixin"))) {
                amResume.setWechat(chatInfo.get("weixin").toString());
            }
            if (CollectionUtils.isNotEmpty(req.getAttachment_resume())){
                amResume.setAttachmentResume(JSONObject.toJSONString(req.getAttachment_resume()));
            }
            boolean result = amResumeService.updateById(amResume);
            log.info("ExtractResumeDataProcessor dealBossNewMessage update amResume result={}", result);
        }else {
            if (Objects.nonNull(chatInfo.get("encryptUid"))){
            amResume.setEncryptGeekId(chatInfo.get("encryptUid").toString());
            }
            if (Objects.nonNull(chatInfo.get("uid"))) {
                amResume.setUid(chatInfo.get("uid").toString());
            }
            if (CollectionUtils.isNotEmpty(req.getAttachment_resume())){
                amResume.setAttachmentResume(JSONObject.toJSONString(req.getAttachment_resume()));
            }
            amResume.setAdminId(amZpLocalAccouts.getAdminId());
            amResume.setAccountId(amZpLocalAccouts.getId());
            amResume.setType(0);
            if (Objects.nonNull(chatInfo.get("toPositionId"))) {
                String toPositionId = chatInfo.get("toPositionId").toString();
                LambdaQueryWrapper<AmPosition> positionQueryWrapper = new LambdaQueryWrapper<>();
                positionQueryWrapper.eq(AmPosition::getEncryptId, toPositionId);
                AmPosition amPositionServiceOne = amPositionService.getOne(positionQueryWrapper, false);
                if (Objects.nonNull(amPositionServiceOne)) {
                    amResume.setPostId(amPositionServiceOne.getId());
                    amResume.setPosition(amPositionServiceOne.getName());
                }
            }

            boolean result = amResumeService.save(amResume);
            log.info("ExtractResumeDataProcessor dealBossNewMessage save amResume result={}", result);
        }

        return ResultVO.success();
    }


}
