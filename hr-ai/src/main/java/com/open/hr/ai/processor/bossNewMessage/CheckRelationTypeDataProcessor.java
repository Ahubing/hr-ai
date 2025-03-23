package com.open.hr.ai.processor.bossNewMessage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import com.open.hr.ai.constant.RedisKyeConstant;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于判断是否需要请求用户信息
 */
@Order(2)
@Component
@Slf4j
public class CheckRelationTypeDataProcessor implements BossNewMessageProcessor {

    @Resource
    private AmClientTasksServiceImpl amClientTasksService;


    @Resource
    private AmChatbotPositionOptionServiceImpl amChatbotPositionOptionService;
    @Resource
    private AmChatbotOptionsItemsServiceImpl amChatbotOptionsItemsService;
    @Resource
    private AmChatbotGreetTaskServiceImpl amChatbotGreetTaskService;
    @Resource
    private AmChatbotGreetResultServiceImpl amChatbotGreetResultService;


    @Resource
    private JedisClientImpl jedisClient;


    /**
     * 判断用户是否是主动打招呼,此时需要获取用户的全部数据, 需要发request_all_info任务
     *
     * 2025-3-23 不需要此流程
     */
    @Override
    public ResultVO dealBossNewMessage(AtomicInteger statusCode, String platform, AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {


        //        log.info("用户:{} 主动打招呼,请求用户信息 amResume={},bossId={}", req.getUser_id(), amResume, amZpLocalAccouts.getId());
//        if (Objects.isNull(amResume) || StringUtils.isBlank(amResume.getEncryptGeekId())) {
//            log.error("用户信息异常 amResume is null");
//            return ResultVO.fail(404, "用户信息异常");
//        }
//
//        if (Objects.equals(amResume.getType(), ReviewStatusEnums.ABANDON.getStatus())){
//            log.info("用户:{} 主动打招呼,用户状态为不符合,继续流程", amResume.getEncryptGeekId());
//            return ResultVO.fail(404, "用户状态为不符合");
//        }
//
//        // 从未对此用户发起本请求时请求一次
//        LambdaQueryWrapper<AmClientTasks> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(AmClientTasks::getBossId, amZpLocalAccouts.getId());
//        queryWrapper.eq(AmClientTasks::getTaskType, ClientTaskTypeEnums.REQUEST_INFO.getType());
//        queryWrapper.like(AmClientTasks::getData, req.getUser_id());
//        AmClientTasks tasksServiceOne = amClientTasksService.getOne(queryWrapper, false);
//        if (Objects.isNull(tasksServiceOne)) {
//            statusCode.set(1);
//            buildRequestTask(amZpLocalAccouts, req, amResume);
//            dealReChatTask(amResume,amZpLocalAccouts);
//            log.info("用户:{} 主动打招呼,请求用户信息", req.getUser_id());
//        }
//        else {
//            // 判断任务的完成情况,如果非完成,也需要重新发起请求
//            if (!Objects.equals(tasksServiceOne.getStatus(), AmClientTaskStatusEnums.FINISH.getStatus())) {
//                statusCode.set(1);
//                //如果是失败,则重新发起请求
//                if (Objects.equals(tasksServiceOne.getStatus(), AmClientTaskStatusEnums.FAILURE.getStatus())) {
//                    log.info("用户:{} 之前的request_info 任务失败,重新请求请求用户信息", req.getUser_id());
//                }
//                log.info("用户:{} 之前的request_info 任务未完成,待完成请求", req.getUser_id());
//                return ResultVO.success();
//            }
//
//        }
        return ResultVO.success();
    }


    private void buildRequestTask(AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req, AmResume amResume) {
        AmClientTasks amClientTasks = new AmClientTasks();
        amClientTasks.setBossId(amZpLocalAccouts.getId());
        amClientTasks.setTaskType(ClientTaskTypeEnums.REQUEST_INFO.getType());
        amClientTasks.setOrderNumber(ClientTaskTypeEnums.REQUEST_INFO.getOrder());
        HashMap<String, Object> hashMap = new HashMap<>();
        HashMap<String, Object> searchDataMap = new HashMap<>();
        hashMap.put("user_id", req.getUser_id());
        // 生成如下结构     "info_type":[] # "attachment_resume"，"phone"，"wechat"
        hashMap.put("info_type", Collections.singletonList("attachment_resume"));
        if (Objects.nonNull(amResume.getEncryptGeekId())) {
            searchDataMap.put("encrypt_geek_id", amResume.getEncryptGeekId());
        }
        if (Objects.nonNull(amResume.getName())) {
            searchDataMap.put("name", amResume.getName());
        }else {
            if (Objects.nonNull(req.getChat_info().get("name"))) {
                log.info("用户:{} 主动打招呼,请求用户信息", req.getChat_info().get("name"));
                searchDataMap.put("name", req.getChat_info().get("name").toString());
            }
        }

        hashMap.put("search_data", searchDataMap);
        amClientTasks.setData(JSONObject.toJSONString(hashMap));
        amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
        amClientTasks.setCreateTime(LocalDateTime.now());
        amClientTasks.setUpdateTime(LocalDateTime.now());
        amClientTasksService.save(amClientTasks);
    }



    public void dealReChatTask(AmResume amResume,AmZpLocalAccouts amZpLocalAccouts){
        Integer postId = amResume.getPostId();
        if (Objects.isNull(postId)) {
            log.error("用户:{} 主动打招呼,岗位id为空, 不生成打招呼任务,请求用户信息 postId is null", amResume.getEncryptGeekId());
            return;
        }

        String accoutsId = amZpLocalAccouts.getId();
        LambdaQueryWrapper<AmChatbotGreetTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmChatbotGreetTask::getAccountId,accoutsId);
        queryWrapper.eq(AmChatbotGreetTask::getPositionId, postId);
        AmChatbotGreetTask one = amChatbotGreetTaskService.getOne(queryWrapper, false);
        if (Objects.isNull(one)){
            log.info("打招呼任务为空,不支持复聊 bossId={},postId={}",accoutsId,postId);
            return;
        }

        AmChatbotGreetResult amChatbotGreetResult = new AmChatbotGreetResult();
        amChatbotGreetResult.setRechatItem(0);
        amChatbotGreetResult.setSuccess(1);
        amChatbotGreetResult.setAccountId(accoutsId);
        amChatbotGreetResult.setCreateTime(LocalDateTime.now());
        amChatbotGreetResult.setTaskId(one.getId());
        amChatbotGreetResult.setUserId(amResume.getUid());
        /**
         * 3、生成复聊任务, 如果存在复聊方案
         */
        AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(new LambdaQueryWrapper<AmChatbotPositionOption>().eq(AmChatbotPositionOption::getAccountId, amZpLocalAccouts.getId()).eq(AmChatbotPositionOption::getPositionId, postId), false);
        if (Objects.isNull(amChatbotPositionOption)) {
            log.info("复聊任务处理开始, 账号:{}, 未找到对应的职位", amZpLocalAccouts.getId());
            return;
        }
        // 查询第一天的复聊任务
        List<AmChatbotOptionsItems> amChatbotOptionsItems = amChatbotOptionsItemsService.lambdaQuery().eq(AmChatbotOptionsItems::getOptionId, amChatbotPositionOption.getInquiryRechatOptionId()).eq(AmChatbotOptionsItems::getDayNum, 1).list();
        if (Objects.isNull(amChatbotOptionsItems) || amChatbotOptionsItems.isEmpty()) {
            log.info("复聊任务处理开始, 账号:{}, 未找到对应的复聊方案", amZpLocalAccouts.getId());
            return;
        }

        for (AmChatbotOptionsItems amChatbotOptionsItem : amChatbotOptionsItems) {
            // 处理复聊任务, 存入队列里面, 用于定时任务处理
            amChatbotGreetResult.setRechatItem(amChatbotOptionsItem.getId());
            amChatbotGreetResult.setTaskId(one.getId());
            amChatbotGreetResultService.updateById(amChatbotGreetResult);
            Long operateTime = System.currentTimeMillis() + Integer.parseInt(amChatbotOptionsItem.getExecTime())* 1000L;
            Long zadd = jedisClient.zadd(RedisKyeConstant.AmChatBotReChatTask, operateTime, JSONObject.toJSONString(amChatbotGreetResult));
            log.info("复聊任务处理开始, 账号:{}, 复聊任务添加结果:{}", amZpLocalAccouts.getId(), zadd);
        }
    }

}
