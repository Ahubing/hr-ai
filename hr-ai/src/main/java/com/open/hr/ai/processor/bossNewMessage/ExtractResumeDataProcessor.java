package com.open.hr.ai.processor.bossNewMessage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.service.impl.AmResumeServiceImpl;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
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
        String phone = chatInfo.get("phone").toString();
        String weixin = chatInfo.get("weixin").toString();

        String userId = req.getUser_id();
        // 保存用户信息
        LambdaQueryWrapper<AmResume> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmResume::getUid, userId);
        AmResume innerAmResume = amResumeService.getOne(queryWrapper, false);
        log.info("ExtractResumeDataProcessor dealBossNewMessage innerAmResume={}", innerAmResume);
        if (Objects.nonNull(innerAmResume)) {
            if (Objects.isNull(phone) || Objects.isNull(weixin)) {
                log.error("extractData phone or weixin is null,bossId={}", amZpLocalAccouts.getId());
                return ResultVO.fail("phone or weixin is null");
            }
            BeanUtils.copyProperties(innerAmResume, amResume);
            amResume.setPhone(chatInfo.get("phone").toString());
            amResume.setWechat(chatInfo.get("weixin").toString());
            if (CollectionUtils.isNotEmpty(req.getAttachmentResume())){
                amResume.setAttachmentResume(JSONObject.toJSONString(req.getAttachmentResume()));
            }
            amResumeService.updateById(amResume);
        }

        return ResultVO.success();
    }


}
