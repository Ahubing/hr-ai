package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotGreetTask;
import com.open.ai.eros.db.mysql.hr.entity.AmClientTasks;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatbotGreetTaskServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmClientTasksServiceImpl;
import com.open.hr.ai.bean.vo.AmClientTasksVo;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.ai.eros.common.constants.ClientTaskTypeEnums;
import com.open.hr.ai.constant.PositionStatusEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author
 * @Date 2025/1/12 18:22
 */
@Slf4j
@Component
public class AmClientTaskManager {


    @Resource
    private AmClientTasksServiceImpl amClientTasksService;
    @Resource
    private AmChatbotGreetTaskServiceImpl amChatbotGreetTaskService;

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
            String statusDetail = (PositionStatusEnums.POSITION_CLOSE.getStatus().equals(status)) ? "关闭" : "打开";
            amClientTasks.setDetail(statusDetail + amPosition.getName() + "岗位");
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
        return ResultVO.success(amClientTaskData);
    }



    /**
     * 查询正在执行中的任务
     */
    public ResultVO getExecuteTask(String bossId,Integer limit) {
        LambdaQueryWrapper<AmClientTasks> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AmClientTasks::getBossId,bossId);
        lambdaQueryWrapper.le(AmClientTasks::getRetryTimes,3);
        lambdaQueryWrapper.in(AmClientTasks::getStatus,AmClientTaskStatusEnums.START.getStatus(),AmClientTaskStatusEnums.NOT_START.getStatus());
        lambdaQueryWrapper.orderByDesc(AmClientTasks::getOrderNumber);
        lambdaQueryWrapper.orderByAsc(AmClientTasks::getCreateTime);
        if (Objects.nonNull(limit)){
            lambdaQueryWrapper.last("limit "+limit);
        }
        List<AmClientTasksVo> amClientTaskData = new ArrayList<>();
        List<AmClientTasks> amClientTasks = amClientTasksService.list(lambdaQueryWrapper);
        for (AmClientTasks amClientTask : amClientTasks) {
            AmClientTasksVo amClientTasksVo = new AmClientTasksVo();
            amClientTasksVo.setDetail(amClientTask.getDetail());
            amClientTasksVo.setId(amClientTask.getId());
            amClientTasksVo.setSuccessCount(0);
            amClientTasksVo.setTotalCount(1);
            amClientTasksVo.setStatus(amClientTask.getStatus());
            amClientTasksVo.setReason(amClientTask.getReason());
            amClientTasksVo.setTaskType(amClientTask.getTaskType());
            amClientTasksVo.setCreateTime(amClientTask.getCreateTime());
            amClientTasksVo.setUpdateTime(amClientTask.getUpdateTime());
            // 获取任务类型
            if (amClientTask.getTaskType().equals(ClientTaskTypeEnums.SEND_MESSAGE.getType())){
                String subType = amClientTask.getSubType();
                if (StringUtils.isNotBlank(subType)){
                    amClientTasksVo.setTaskType(subType);
                }
            }
            if (amClientTask.getTaskType().equals(ClientTaskTypeEnums.GREET.getType())){
                //提取任务里面的打招呼任务id, 目的是为了获取岗位数据
                JSONObject jsonObject = JSONObject.parseObject(amClientTask.getData());
                if (jsonObject.containsKey("greetId")) {
                    log.info("greetHandle greetId is null,bossId={}", bossId);
                    AmChatbotGreetTask amChatbotGreetTask = amChatbotGreetTaskService.getById(jsonObject.get("greetId").toString());
                    if (Objects.nonNull(amChatbotGreetTask)){
                        amClientTasksVo.setTotalCount(amChatbotGreetTask.getTaskNum());
                        amClientTasksVo.setSuccessCount(amChatbotGreetTask.getDoneNum());
                    }
                }
            }
            amClientTaskData.add(amClientTasksVo);
        }
        return ResultVO.success(amClientTaskData);
    }



    /**
     * 查询已完成的任务
     */
    public ResultVO getDoneTask(String bossId,Integer pageNum,Integer pageSize) {
        LambdaQueryWrapper<AmClientTasks> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AmClientTasks::getBossId,bossId);
        lambdaQueryWrapper.orderByDesc(AmClientTasks::getCreateTime);
        // 不等于开始或者未开始
        lambdaQueryWrapper.notIn(AmClientTasks::getStatus,AmClientTaskStatusEnums.NOT_START.getStatus(),AmClientTaskStatusEnums.START.getStatus());
        Page<AmClientTasks> page = new Page<>(pageNum, pageSize);
        Page<AmClientTasks> amClientTasksPage = amClientTasksService.page(page, lambdaQueryWrapper);
        List<AmClientTasksVo> amClientTaskData = new ArrayList<>();
        for (AmClientTasks amClientTask : amClientTasksPage.getRecords()) {
            AmClientTasksVo amClientTasksVo = new AmClientTasksVo();
            amClientTasksVo.setDetail(amClientTask.getDetail());
            amClientTasksVo.setId(amClientTask.getId());
            amClientTasksVo.setSuccessCount(1);
            amClientTasksVo.setTotalCount(1);
            amClientTasksVo.setStatus(AmClientTaskStatusEnums.FINISH.getStatus());
            amClientTasksVo.setTaskType(amClientTask.getTaskType());
            amClientTasksVo.setReason(amClientTask.getReason());
            amClientTasksVo.setCreateTime(amClientTask.getCreateTime());
            amClientTasksVo.setUpdateTime(amClientTask.getUpdateTime());
            if (amClientTask.getStatus().equals(AmClientTaskStatusEnums.FAILURE.getStatus())){
                amClientTasksVo.setSuccessCount(0);
                amClientTasksVo.setStatus(AmClientTaskStatusEnums.FAILURE.getStatus());
            }
            // 获取任务类型
            if (amClientTask.getTaskType().equals(ClientTaskTypeEnums.SEND_MESSAGE.getType())){
                String subType = amClientTask.getSubType();
                if (StringUtils.isNotBlank(subType)){
                    amClientTasksVo.setTaskType(subType);
                }
            }
            if (amClientTask.getTaskType().equals(ClientTaskTypeEnums.GREET.getType())){
                //提取任务里面的打招呼任务id, 目的是为了获取岗位数据
                JSONObject jsonObject = JSONObject.parseObject(amClientTask.getData());
                if (jsonObject.containsKey("greetId")) {
                    log.info("greetHandle greetId is null,bossId={}", bossId);
                    AmChatbotGreetTask amChatbotGreetTask = amChatbotGreetTaskService.getById(jsonObject.get("greetId").toString());
                    if (Objects.nonNull(amChatbotGreetTask)){
                        amClientTasksVo.setTotalCount(amChatbotGreetTask.getTaskNum());
                        amClientTasksVo.setSuccessCount(amChatbotGreetTask.getDoneNum());
                    }
                }
            }
            amClientTaskData.add(amClientTasksVo);
        }
        return ResultVO.success(PageVO.build(amClientTasksPage.getTotal(), amClientTaskData));
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
                    lambdaQueryWrapper.eq(AmClientTasks::getSubType, ClientTaskTypeEnums.SEND_RECHAT_MESSAGE.getSubType());
                } else if (ClientTaskTypeEnums.SEND_MESSAGE.getType().equals(taskType)) {
                    lambdaQueryWrapper.eq(AmClientTasks::getTaskType, taskType);
                } else if (ClientTaskTypeEnums.GREET.getType().equals(taskType)) {
                    lambdaQueryWrapper.eq(AmClientTasks::getTaskType, taskType);
                } else if ("other".equals(taskType)) {
                    lambdaQueryWrapper.notIn(AmClientTasks::getTaskType, "rechat", ClientTaskTypeEnums.SEND_MESSAGE.getType(), ClientTaskTypeEnums.GREET.getType());
                } else {
                    lambdaQueryWrapper.eq(AmClientTasks::getTaskType, taskType);
                }
            }
            // status 为 0 和 1
            lambdaQueryWrapper.in(AmClientTasks::getStatus, AmClientTaskStatusEnums.NOT_START.getStatus(), AmClientTaskStatusEnums.START.getStatus());
            // 将status 设置为 2,并且原因为 用户设置失效
            lambdaQueryWrapper.set(AmClientTasks::getStatus, AmClientTaskStatusEnums.FAILURE.getStatus());
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
