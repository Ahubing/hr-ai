package com.open.hr.ai.processor.bossNewMessage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmClientTasks;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.service.impl.AmClientTasksServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmResumeServiceImpl;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于分析当前用户的prompt
 */
@Order(2)
@Component
@Slf4j
public class CheckRelationTypeDataProcessor implements BossNewMessageProcessor {

    @Resource
    private AmClientTasksServiceImpl amClientTasksService;

    /**
     * 判断用户是否是主动打招呼,此时需要获取用户的全部数据, 需要发request_all_info任务
     */
    @Override
    public ResultVO dealBossNewMessage(AtomicInteger statusCode, String platform, AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {
        log.info("用户:{} 主动打招呼,请求用户信息 amResume={},bossId={}", req.getUser_id(), amResume, amZpLocalAccouts.getId());
        if (Objects.isNull(amResume) || StringUtils.isBlank(amResume.getEncryptGeekId())) {
            log.error("用户信息异常 amResume is null");
            return ResultVO.fail(404, "用户信息异常");
        }

        // 从未对此用户发起本请求时请求一次
        LambdaQueryWrapper<AmClientTasks> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmClientTasks::getBossId, amZpLocalAccouts.getId());
        queryWrapper.eq(AmClientTasks::getTaskType, ClientTaskTypeEnums.REQUEST_INFO.getType());
        queryWrapper.like(AmClientTasks::getData, req.getUser_id());
        AmClientTasks tasksServiceOne = amClientTasksService.getOne(queryWrapper, false);
        if (Objects.isNull(tasksServiceOne)) {
            statusCode.set(1);
            buildRequestTask(amZpLocalAccouts, req, amResume);
            log.info("用户:{} 主动打招呼,请求用户信息", req.getUser_id());
        }
        else {
            // 判断任务的完成情况,如果非完成,也需要重新发起请求
            if (!Objects.equals(tasksServiceOne.getStatus(), AmClientTaskStatusEnums.FINISH.getStatus())) {
                statusCode.set(1);
                //如果是失败,则重新发起请求
                if (Objects.equals(tasksServiceOne.getStatus(), AmClientTaskStatusEnums.FAILURE.getStatus())) {
                    log.info("用户:{} 之前的request_info 任务失败,重新请求请求用户信息", req.getUser_id());
                }
                log.info("用户:{} 主动打招呼,请求用户信息", req.getUser_id());
                return ResultVO.success();
            }

        }
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


}
