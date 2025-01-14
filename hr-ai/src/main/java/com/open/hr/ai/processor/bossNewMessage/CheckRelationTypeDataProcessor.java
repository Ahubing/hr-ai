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
import com.open.hr.ai.processor.BossNewMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

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
     * 判断用户是否是主动打招呼,如果是判断releationType是不是等于6
     */
    @Override
    public ResultVO dealBossNewMessage(AmResume amResume, AmZpLocalAccouts amZpLocalAccouts, ClientBossNewMessageReq req) {

        if (Objects.isNull(amResume)){
            return ResultVO.fail(404,"用户信息异常");
        }

        JSONObject chatInfo = req.getChat_info();
        Object releationType = chatInfo.get("releationType");
        if (Objects.nonNull(releationType) && Integer.parseInt(releationType.toString()) == 6) {
            // 从未对此用户发起本请求时请求一次
            LambdaQueryWrapper<AmClientTasks> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmClientTasks::getBossId, amZpLocalAccouts.getId());
            queryWrapper.eq(AmClientTasks::getTaskType, "request_all_info");
            queryWrapper.like(AmClientTasks::getData, req.getUser_id());
            AmClientTasks tasksServiceOne = amClientTasksService.getOne(queryWrapper, false);
            if (Objects.isNull(tasksServiceOne)) {
                AmClientTasks amClientTasks = new AmClientTasks();
                amClientTasks.setBossId(amZpLocalAccouts.getId());
                amClientTasks.setTaskType("request_all_info");
                HashMap<String, Object> hashMap = new HashMap<>();
                HashMap<String, Object> searchDataMap = new HashMap<>();
                hashMap.put("user_id", req.getUser_id());
                searchDataMap.put("encrypt_friend_id", amResume.getEncryptGeekId());
                searchDataMap.put("name", amResume.getName());
                hashMap.put("search_data", searchDataMap);
                amClientTasks.setData(JSONObject.toJSONString(hashMap));
                amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
                amClientTasks.setCreateTime(LocalDateTime.now());
                amClientTasks.setUpdateTime(LocalDateTime.now());
                amClientTasksService.save(amClientTasks);
            }
        }
        return ResultVO.success();
    }


}
