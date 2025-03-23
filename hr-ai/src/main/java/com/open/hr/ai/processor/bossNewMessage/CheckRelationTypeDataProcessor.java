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

    @Resource
    private ReplyUserMessageDataProcessor replyUserMessageDataProcessor;


    /**
     * 判断用户是否是主动打招呼,此时需要获取用户的全部数据, 需要发request_all_info任务
     *
     * 2025-3-23 不需要此流程
     */
    @Override
    public ResultVO dealBossNewMessage(AtomicInteger statusCode, String platform, AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {


        log.info("用户:{} 主动打招呼,请求用户信息 amResume={},bossId={}", req.getUser_id(), amResume, amZpLocalAccouts.getId());
        if (Objects.isNull(amResume) || StringUtils.isBlank(amResume.getEncryptGeekId())) {
            log.error("用户信息异常 amResume is null");
            return ResultVO.fail(404, "用户信息异常");
        }

        if (Objects.equals(amResume.getType(), ReviewStatusEnums.ABANDON.getStatus())){
            log.info("用户:{} 主动打招呼,用户状态为不符合,继续流程", amResume.getEncryptGeekId());
            return ResultVO.fail(404, "用户状态为不符合");
        }

        // 从未对此用户发起本请求时请求一次
        LambdaQueryWrapper<AmClientTasks> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmClientTasks::getBossId, amZpLocalAccouts.getId());
        queryWrapper.eq(AmClientTasks::getTaskType, ClientTaskTypeEnums.REQUEST_INFO.getType());
        queryWrapper.like(AmClientTasks::getData, req.getUser_id());
        AmClientTasks tasksServiceOne = amClientTasksService.getOne(queryWrapper, false);
        if (Objects.isNull(tasksServiceOne)) {
            statusCode.set(1);
            replyUserMessageDataProcessor.buildRequestTask(amZpLocalAccouts, Integer.parseInt(amResume.getUid()), amResume,false);
            log.info("用户:{} 主动打招呼,没有用户信息, 需要拦截本次请求, ", req.getUser_id());
        }
        else {
            queryWrapper.eq(AmClientTasks::getStatus, AmClientTaskStatusEnums.FINISH.getStatus());
            int count = amClientTasksService.count(queryWrapper);
            if (count == 0) {
                statusCode.set(1);
                log.info("用户:{} 主动打招呼,之前的request_info 任务未完成,待完成请求,先拦截请求", req.getUser_id());
            }
        }
        return ResultVO.success();
    }




}
