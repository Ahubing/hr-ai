package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmClientTasks;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.service.impl.AmClientTasksServiceImpl;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.ClientTaskTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author
 * @Date 2025/1/12 18:22
 */
@Slf4j
@Component
public class AmClientTaskManager {


    @Resource
    private AmClientTasksServiceImpl amClientTasksService;

    /**
     * 批量关闭打开岗位
     */
    public Boolean batchCloseOrOpenPosition(String bossId, AmPosition amPosition, Integer status) {
        try {
            AmClientTasks amClientTasks = new AmClientTasks();
            amClientTasks.setBossId(bossId);
            amClientTasks.setCreateTime(LocalDateTime.now());
            amClientTasks.setTaskType(ClientTaskTypeEnums.SWITCH_JOB_STATE.getType());
            amClientTasks.setOrderNumber(ClientTaskTypeEnums.SWITCH_JOB_STATE.getOrder());
            amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("encrypt_id", amPosition.getEncryptId());
            amClientTasks.setData(jsonObject.toJSONString());
            boolean result = amClientTasksService.save(amClientTasks);
            log.info("batchCloseOrOpenPosition amClientTasks={} result={}", amClientTasks, result);
        } catch (Exception e) {
            log.error("batchCloseOrOpenPosition error", e);
            return false;
        }
        return true;
    }

    /**
     * 查询任务池里面的数据
     */
    public ResultVO<Map<String, Integer>> getTaskList(String bossId) {
        Map<String, Integer> amClientTaskData = amClientTasksService.getAmClientTaskData(bossId);
        log.info("getTaskList amClientTaskData={}", amClientTaskData);
        return ResultVO.success(amClientTaskData);
    }


    /**
     * 删除任务池里面的数据
     */
    public ResultVO<Map<String, Integer>> deleteAmClientTask(String bossId, String taskType) {
        try {
            log.info("deleteAmClientTask bossId={} taskType={}", bossId, taskType);
            LambdaUpdateWrapper<AmClientTasks> lambdaQueryWrapper = new LambdaUpdateWrapper<>();
            lambdaQueryWrapper.eq(AmClientTasks::getBossId, bossId);
            // 如果taskType 为空,则清空全部任务
            if (StringUtils.isNotBlank(taskType)) {
                if ("rechat".equals(taskType)) {
                    lambdaQueryWrapper.eq(AmClientTasks::getTaskType, ClientTaskTypeEnums.SEND_MESSAGE.getType());
                    lambdaQueryWrapper.eq(AmClientTasks::getSubType, "rechat");
                } else if (ClientTaskTypeEnums.SEND_MESSAGE.getType().equals(taskType)) {
                    lambdaQueryWrapper.eq(AmClientTasks::getTaskType, taskType);
                    lambdaQueryWrapper.eq(AmClientTasks::getData, ClientTaskTypeEnums.SEND_MESSAGE.getType());
                }else {
                    lambdaQueryWrapper.eq(AmClientTasks::getTaskType, taskType);
                }
            }
            // status 为 0 和 1
            lambdaQueryWrapper.in(AmClientTasks::getStatus, AmClientTaskStatusEnums.NOT_START.getStatus(), AmClientTaskStatusEnums.START.getStatus());
            // 将status 设置为 2,并且原因为 用户设置失效
            lambdaQueryWrapper.set(AmClientTasks::getStatus, AmClientTaskStatusEnums.FINISH.getStatus());
            lambdaQueryWrapper.set(AmClientTasks::getReason, "用户设置失效");

            boolean result = amClientTasksService.update(lambdaQueryWrapper);
            log.info("deleteAmClientTask result={}", result);
            return  ResultVO.success();
        } catch (Exception e) {
            log.error("deleteAmClientTask error", e);
        }
        return ResultVO.fail("系统异常");

    }

}
