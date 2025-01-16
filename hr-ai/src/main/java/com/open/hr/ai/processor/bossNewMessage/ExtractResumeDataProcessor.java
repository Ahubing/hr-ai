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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
    public ResultVO dealBossNewMessage(AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {
       log.info("ExtractResumeDataProcessor dealBossNewMessage amResume={},amZpLocalAccouts={},req={}",amResume,amZpLocalAccouts,req);
        JSONObject chatInfo = req.getChat_info();
        JSONObject chatData = chatInfo.getJSONObject("data");
            if (Objects.isNull(chatData) || chatData.isEmpty()){
                log.error("extractData chatData is null,bossId={}",amZpLocalAccouts.getId());
                return ResultVO.fail("chatData is null");
            }
            String userId = req.getUser_id();
            // 保存用户信息
            LambdaQueryWrapper<AmResume> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmResume::getUid, userId);

         AmResume innerAmResume = amResumeService.getOne(queryWrapper, false);
         log.info("ExtractResumeDataProcessor dealBossNewMessage innerAmResume={}",innerAmResume);
        if (Objects.isNull(innerAmResume)){
                amResume.setAdminId(amZpLocalAccouts.getAdminId());
                amResume.setAccountId(amZpLocalAccouts.getId());
                amResume.setUid(Objects.nonNull(chatData.get("uid")) ? chatData.get("uid").toString() : "");
                amResume.setEncryptGeekId(Objects.nonNull(chatData.get("encryptUid")) ? chatData.get("encryptUid").toString() : "");
                amResume.setCity(Objects.nonNull(chatData.get("city")) ? chatData.get("city").toString() : "");
                amResume.setAge(0);
                amResume.setApplyStatus(Objects.nonNull(chatData.get("positionStatus")) ? chatData.get("positionStatus").toString() : "");
                amResume.setCompany(Objects.nonNull(chatData.get("lastCompany")) ? chatData.get("lastCompany").toString() : "");
                amResume.setAvatar(Objects.nonNull(chatData.get("avatar")) ? chatData.get("avatar").toString() : "");
                amResume.setEducation(Objects.nonNull(chatData.get("school")) ? chatData.get("school").toString() : "");
                amResume.setCreateTime(LocalDateTime.now());
                amResume.setExperiences(Objects.nonNull(chatData.get("workExpList")) ? chatData.get("workExpList").toString() : "");
                amResume.setJobSalary(Objects.nonNull(chatData.get("salaryDesc")) ? chatData.get("salaryDesc").toString() : "");
                amResume.setGender(Objects.nonNull(chatData.get("gender")) ? Integer.parseInt(chatData.get("gender").toString()) : 0);
                amResume.setPlatform("BOSS直聘");
                amResume.setJobSalary(Objects.nonNull(chatData.get("salaryDesc")) ? chatData.get("salaryDesc").toString() : "");
                amResume.setType(0);
                amResume.setSalary(Objects.nonNull(chatData.get("salaryDesc")) ? chatData.get("salaryDesc").toString() : "");
                amResume.setPosition(Objects.nonNull(chatData.get("toPosition")) ? chatData.get("toPosition").toString() : "");
                amResume.setName(Objects.nonNull(chatData.get("name")) ? chatData.get("name").toString() : "");
                amResume.setAccountId(amZpLocalAccouts.getId());
                amResume.setPhone(Objects.nonNull(chatData.get("phone")) ? chatData.get("phone").toString() : "");
                amResume.setWechat(Objects.nonNull(chatData.get("weixin")) ? chatData.get("weixin").toString() : "");
                amResumeService.save(amResume);
            }else {
                amResume = innerAmResume;
                amResume.setPhone(chatData.get("phone").toString());
                amResume.setWechat(chatData.get("weixin").toString());
                amResumeService.updateById(amResume);
            }
            return ResultVO.success();
        }


}
