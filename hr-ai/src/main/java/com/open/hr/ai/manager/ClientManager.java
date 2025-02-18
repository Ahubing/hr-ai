package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.bean.req.ClientFinishTaskReq;
import com.open.hr.ai.bean.req.ClientQrCodeReq;
import com.open.hr.ai.constant.*;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @Date 2025/1/6 20:00
 */
@Slf4j
@Component
public class ClientManager {

    @Resource
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;
    @Resource
    private AmPositionSectionServiceImpl amPositionSectionService;
    @Resource
    private AmPositionServiceImpl amPositionService;
    @Resource
    private AmClientTasksServiceImpl amClientTasksService;

    @Resource
    private AmPositionSyncTaskServiceImpl amPositionSyncTaskService;
    @Resource
    private AmResumeServiceImpl amResumeService;

    @Resource
    private AmChatbotGreetConfigServiceImpl amChatbotGreetConfigService;

    @Resource
    private AmChatbotGreetResultServiceImpl amChatbotGreetResultService;

    @Resource
    private AmChatbotGreetTaskServiceImpl amChatbotGreetTaskService;

    @Resource
    private AmChatbotPositionOptionServiceImpl amChatbotPositionOptionService;
    @Resource
    private AmChatbotOptionsItemsServiceImpl amChatbotOptionsItemsService;
    @Resource
    private AmZpPlatformsServiceImpl amZpPlatformsService;
    @Resource
    private AmChatMessageServiceImpl amChatMessageService;

    @Resource
    private AmChatbotGreetConditionServiceImpl amChatbotGreetConditionService;

    @Resource
    private JedisClientImpl jedisClient;

    @Autowired
    private List<BossNewMessageProcessor> bossNewMessageProcessors;


    public ResultVO connectClient(String platform,String bossId, String connectId) {
        try {

            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail(404, "boss_id不存在");
            }
            if (AmLocalAccountStatusEnums.FREE.getStatus().equals(amZpLocalAccouts.getState())) {
                // 规定超过25秒就认定下线
                if (Objects.nonNull(amZpLocalAccouts.getUpdateTime()) && System.currentTimeMillis() - DateUtils.convertLocalDateTimeToTimestamp(amZpLocalAccouts.getUpdateTime()) < 25 * 1000) {
                    return ResultVO.fail(409, "boss_id 已在线");
                }
            }
            amZpLocalAccouts.setUpdateTime(LocalDateTime.now());
            amZpLocalAccouts.setBrowserId(connectId);
            amZpLocalAccouts.setState(AmLocalAccountStatusEnums.WAIT_LOGIN.getStatus());
            amZpLocalAccouts.setExtra("");
            amZpLocalAccoutsService.updateById(amZpLocalAccouts);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("客户端连接异常 bossId={},connectId={}", bossId, connectId, e);
        }
        return ResultVO.fail(409, "客户端连接异常");
    }

    public ResultVO loginClient(String platform,String bossId, String connectId, String extBossId) {
        try {
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail(404, "boss_id不存在");
            }
            if (StringUtils.isNotBlank(amZpLocalAccouts.getExtBossId())) {
                if (!extBossId.equals(amZpLocalAccouts.getExtBossId())) {
                    return ResultVO.fail(409, "验证失败，与存储的数值不一致");
                }
            } else {
                amZpLocalAccouts.setExtBossId(extBossId);
            }
            amZpLocalAccouts.setUpdateTime(LocalDateTime.now());
            amZpLocalAccouts.setBrowserId(connectId);
            amZpLocalAccouts.setState(AmLocalAccountStatusEnums.FREE.getStatus());
            amZpLocalAccoutsService.updateById(amZpLocalAccouts);
            LambdaQueryWrapper<AmClientTasks> lambdaQueryWrapper = new QueryWrapper<AmClientTasks>().lambda();
            lambdaQueryWrapper.eq(AmClientTasks::getBossId, bossId);
            lambdaQueryWrapper.eq(AmClientTasks::getTaskType, ClientTaskTypeEnums.GET_ALL_JOB.getType());
            lambdaQueryWrapper.le(AmClientTasks::getStatus, AmClientTaskStatusEnums.START.getStatus());
            int count = amClientTasksService.count(lambdaQueryWrapper);
            if (count > 0) {
                log.info("账号:{} 存在未完成的任务", bossId);
                return ResultVO.success();
            }

            HashMap<String, Object> map = new HashMap<>();
            map.put("boss_id", amZpLocalAccouts.getId());
            map.put("browser_id", amZpLocalAccouts.getBrowserId());
            map.put("page", 1);
            AmClientTasks amClientTasks = new AmClientTasks();
            amClientTasks.setId(UUID.randomUUID().toString());
            amClientTasks.setBossId(amZpLocalAccouts.getId());
            amClientTasks.setTaskType(ClientTaskTypeEnums.GET_ALL_JOB.getType());
            amClientTasks.setOrderNumber(ClientTaskTypeEnums.GET_ALL_JOB.getOrder());
            amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
            amClientTasks.setData(JSONObject.toJSONString(map));
            amClientTasks.setCreateTime(LocalDateTime.now());
            amClientTasks.setUpdateTime(LocalDateTime.now());
            boolean result = amClientTasksService.save(amClientTasks);
            log.info("amClientTasksService save result={},amClientTasks={}", result, amClientTasks);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("客户端登录异常 bossId={},connectId={},extBossId={}", bossId, connectId, extBossId, e);
        }
        return ResultVO.fail(409, "登录失败");
    }


    public ResultVO updateClientStatus(String platform,String bossId, String connectId, String inputStatus) {
        try {
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail(404, "boss_id不存在");
            }
            if (!amZpLocalAccouts.getBrowserId().equals(connectId)) {
                return ResultVO.fail(401, "connect_id 不一致");
            }
            amZpLocalAccouts.setUpdateTime(LocalDateTime.now());
            amZpLocalAccouts.setBrowserId(connectId);
            amZpLocalAccouts.setState(inputStatus);
            if (AmLocalAccountStatusEnums.OFFLINE.getStatus().equals(inputStatus)) {
                amZpLocalAccouts.setExtra("");
                amZpLocalAccouts.setBrowserId("");
            }

            boolean result = amZpLocalAccoutsService.updateById(amZpLocalAccouts);
            log.info("updateClientStatus result={},bossId={}",result,bossId);
            JSONObject jsonObject = new JSONObject();
            AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(new LambdaQueryWrapper<AmChatbotGreetConfig>().eq(AmChatbotGreetConfig::getAccountId, bossId), false);
            if (Objects.nonNull(amChatbotGreetConfig)) {
                jsonObject.put("isGreetOn",amChatbotGreetConfig.getIsGreetOn());
                jsonObject.put("isRechatOn",amChatbotGreetConfig.getIsRechatOn());
                jsonObject.put("isAiOn",amChatbotGreetConfig.getIsAiOn());
                jsonObject.put("isAllOn",amChatbotGreetConfig.getIsAllOn());
            }
            return ResultVO.success(jsonObject);
        } catch (Exception e) {
            log.error("客户端状态更新异常 bossId={},connectId={},status={}", bossId, connectId, inputStatus, e);
        }
        return ResultVO.fail(409, "客户端状态更新异常");
    }


    public ResultVO loginQrCodeSave(String platform,String bossId, String connectId, ClientQrCodeReq req) {
        try {
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail(404, "boss_id不存在");
            }
            if (!amZpLocalAccouts.getBrowserId().equals(connectId)) {
                return ResultVO.fail(401, "connect_id 不一致");
            }
            amZpLocalAccouts.setUpdateTime(LocalDateTime.now());
            amZpLocalAccouts.setBrowserId(connectId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("qr_code", req.getQr_code());
            jsonObject.put("expires", req.getExpires());
            amZpLocalAccouts.setExtra(JSONObject.toJSONString(jsonObject));
            amZpLocalAccoutsService.updateById(amZpLocalAccouts);
            return ResultVO.success("二维码更新成功");
        } catch (Exception e) {
            log.error("客户端状态更新异常 bossId={},connectId={},req={}", bossId, connectId, JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail(409, "客户端二维码更新异常");
    }

    public ResultVO getClientTask(String platform,String bossId, String connectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail(404, "boss_id不存在");
            }
            if (!amZpLocalAccouts.getBrowserId().equals(connectId)) {
                return ResultVO.fail(401, "connect_id 不一致");
            }
            LambdaQueryWrapper<AmClientTasks> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmClientTasks::getBossId, bossId);
            queryWrapper.le(AmClientTasks::getStatus, 1);
            queryWrapper.le(AmClientTasks::getRetryTimes, 2);
            queryWrapper.orderByDesc(AmClientTasks::getOrderNumber);
            queryWrapper.orderByAsc(AmClientTasks::getCreateTime);
            AmClientTasks amClientTasks = amClientTasksService.getOne(queryWrapper, false);
            if (Objects.nonNull(amClientTasks)) {
                amClientTasks.setRetryTimes(amClientTasks.getRetryTimes() + 1);
                jsonObject.put("boss_id", bossId);
                jsonObject.put("task_type", amClientTasks.getTaskType());
                jsonObject.put("task_id", amClientTasks.getId());
                jsonObject.put("data", amClientTasks.getData());
                amClientTasks.setStatus(1);
                amClientTasksService.updateById(amClientTasks);
            }
            return ResultVO.success(jsonObject);
        } catch (Exception e) {
            log.error("获取任务异常 bossId={},connectId={}", bossId, connectId, e);
        }
        // 异常也返回为空
        return ResultVO.success(jsonObject);
    }

    public ResultVO bossNewMessage(String platform,String bossId, String connectId, ClientBossNewMessageReq req) {

        try {
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail(404, "boss_id不存在");
            }

            if (!amZpLocalAccouts.getBrowserId().equals(connectId)) {
                return ResultVO.fail(401, "connect_id 不一致");
            }

            AmChatbotGreetResult resultServiceOne = amChatbotGreetResultService.getOne(new QueryWrapper<AmChatbotGreetResult>().eq("account_id", bossId).eq("user_id", req.getUser_id()).eq("success", 1), false);
            if (Objects.nonNull(resultServiceOne)) {
                resultServiceOne.setSuccess(2);
                amChatbotGreetResultService.updateById(resultServiceOne);
            }

            AmResume amResume = new AmResume();
            for (BossNewMessageProcessor bossNewMessageProcessor : bossNewMessageProcessors) {
                bossNewMessageProcessor.dealBossNewMessage(platform,amResume, amZpLocalAccouts, req);
            }
            return ResultVO.success();
        } catch (Exception e) {
            log.error("保存数据异常 bossId={},connectId={},req={}", bossId, connectId, JSONObject.toJSONString(req), e);
        }
        // 异常也返回为空
        return ResultVO.success();
    }


    public ResultVO finishClientTask(String platform,String bossId, String connectId, ClientFinishTaskReq clientFinishTaskReq) {
        JSONObject jsonObject = new JSONObject();
        try {
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail(404, "boss_id不存在");
            }
            if (!amZpLocalAccouts.getBrowserId().equals(connectId)) {
                return ResultVO.fail(401, "connect_id 不一致");
            }

            String taskId = clientFinishTaskReq.getTask_id();
            Boolean success = clientFinishTaskReq.getSuccess();
            String reason = clientFinishTaskReq.getReason();
            JSONObject data = clientFinishTaskReq.getData();
            AmClientTasks tasksServiceOne = amClientTasksService.getById(taskId);
            if (Objects.isNull(tasksServiceOne)) {
                return ResultVO.fail("task_id不存在");
            }
            if (success) {
                tasksServiceOne.setStatus(AmClientTaskStatusEnums.FINISH.getStatus());
                tasksServiceOne.setUpdateTime(LocalDateTime.now());
            } else {
                if (tasksServiceOne.getRetryTimes() < 3) {
                    return ResultVO.success("任务要重试");
                }

                tasksServiceOne.setStatus(AmClientTaskStatusEnums.FAILURE.getStatus());
                tasksServiceOne.setUpdateTime(LocalDateTime.now());
                tasksServiceOne.setReason(reason);
                dealErrorTask(bossId,taskId, tasksServiceOne);
            }
            boolean result = amClientTasksService.updateById(tasksServiceOne);
            log.info("amClientTasksService update result={},tasksServiceOne={}", result, tasksServiceOne);
            String taskType = tasksServiceOne.getTaskType();
            switch (taskType) {
                case "get_all_job":
                    getAllJobHandle(platform,bossId, data);
                    break;
                case "greet":
                    greetHandle(platform,tasksServiceOne, taskId, bossId, data);
                    break;
                case "request_info":
                    dealUserAllInfoData(platform,taskId, amZpLocalAccouts, data);
                    break;
                case "switch_job_state":
                    switchJobState(taskId, bossId, data);
                    break;
                default:
                    log.error("找不到该任务 req={}", clientFinishTaskReq);
                    break;
            }
            return ResultVO.success();
        } catch (Exception e) {
            log.error("结束任务异常 bossId={},connectId={},req={}", bossId, connectId, JSONObject.toJSONString(clientFinishTaskReq), e);
        }
        // 异常也返回为空
        return ResultVO.success(jsonObject);
    }


    /**
     * 同步职位结果数据处理
     */
    private void getAllJobHandle(String platform,String bossId, JSONObject finishTaskReqData) {
        try {
            LambdaUpdateWrapper<AmPositionSyncTask> queryWrapper = new LambdaUpdateWrapper<>();
            queryWrapper.eq(AmPositionSyncTask::getAccountId, bossId).set(AmPositionSyncTask::getStatus, 2);
            amPositionSyncTaskService.update(queryWrapper);
            savePosition(bossId, platform, finishTaskReqData);
        } catch (Exception e) {
            log.error("syncPositionResultData异常 bossId={},finishTaskReqData={}", bossId, finishTaskReqData, e);
        }
    }

    private void savePosition(String bossId, String platForm, JSONObject jsonObject) {
        try {

            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                log.error("savePosition  amZpLocalAccouts is null,bossId={}", bossId);
                return;
            }
            LambdaQueryWrapper<AmZpPlatforms> zpPlatformsLambdaQueryWrapper = new LambdaQueryWrapper<>();
            zpPlatformsLambdaQueryWrapper.eq(AmZpPlatforms::getPlatformCode, platForm);
            AmZpPlatforms amZpPlatforms = amZpPlatformsService.getOne(zpPlatformsLambdaQueryWrapper, false);
            if (Objects.isNull(amZpPlatforms)) {
                //如果为空,默认取第一个
                amZpPlatforms = amZpPlatformsService.getById(1);
            }

            // 查询部门信息
            LambdaQueryWrapper<AmPositionSection> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmPositionSection::getAdminId, amZpLocalAccouts.getAdminId());
            AmPositionSection amPositionSection = amPositionSectionService.getOne(queryWrapper, false);
            if (Objects.isNull(amPositionSection)) {
                amPositionSection = new AmPositionSection();
                amPositionSection.setName("默认");
                amPositionSection.setAdminId(amZpLocalAccouts.getAdminId());
                amPositionSectionService.save(amPositionSection);
            }

            //开始
            Integer sectionId = amPositionSection.getId();
            // 同步结束
            amZpLocalAccouts.setIsSync(2);

            // 注意入参格式
            JSONArray jobsArray = jsonObject.getJSONArray("jobs");
            if (Objects.isNull(jobsArray) || jobsArray.isEmpty()) {
                log.error("savePosition jobsArray is null,bossId={},platForm{} jsonObject={}", bossId, platForm, jsonObject);
                return;
            }
            // 加密岗位id, 用于删除岗位状态
            List<String> encryptIds = new ArrayList<>();
            for (int i = 0; i < jobsArray.size(); i++) {
                try {
                    JSONObject jobData = jobsArray.getJSONObject(i);
//                    JSONArray innerDatas = arrayJSONObject.getJSONArray("data");
//                    if (Objects.isNull(innerDatas) || innerDatas.isEmpty()) {
//                        log.info("savePosition innerDatas is null,bossId={},platForm{},i={}", bossId, platForm, i);
//                        return;
//                    }

                    String jobName = jobData.get("jobName").toString();
                    String encryptId = jobData.get("encryptId").toString();
                    if (StringUtils.isBlank(jobName) || StringUtils.isBlank(encryptId)) {
                        log.error("savePosition jobName or encryptId is null,bossId={},platForm{},i={}", bossId, platForm, i);
                        return;
                    }
                    encryptIds.add(encryptId);


                    //查询出全部的岗位数据,进行处理
                    LambdaQueryWrapper<AmPosition> positionQueryWrapper = new LambdaQueryWrapper<>();
                    positionQueryWrapper.eq(AmPosition::getBossId, bossId);
                    positionQueryWrapper.eq(AmPosition::getEncryptId,encryptId);
                    AmPosition amPosition = amPositionService.getOne(positionQueryWrapper,false);

                    int jobStatus = jobData.get("jobStatus").toString().equals("1") ? 1 : 0;

                    if (Objects.nonNull(amPosition)) {
                        amPosition.setEncryptId(encryptId);
                        amPosition.setIsOpen(jobStatus);
                        amPosition.setName(jobName);
                        amPosition.setExtendParams(jobData.toJSONString());
                        amPositionService.updateById(amPosition);
                    } else {
                        AmPosition newAmPosition = new AmPosition();
                        newAmPosition.setAdminId(amZpLocalAccouts.getAdminId());
                        newAmPosition.setName(jobName);
                        newAmPosition.setSectionId(sectionId);
                        newAmPosition.setBossId(bossId);
                        newAmPosition.setUid(0);
                        newAmPosition.setChannel(amZpPlatforms.getId());
                        newAmPosition.setEncryptId(encryptId);
                        newAmPosition.setIsOpen(jobStatus);
                        newAmPosition.setCreateTime(LocalDateTime.now());
                        newAmPosition.setExtendParams(jobData.toJSONString());
                        boolean saveResult = amPositionService.save(newAmPosition);
                        log.info("amPositionService save result={}, amPosition={}", saveResult, newAmPosition);
                    }
                    // 如果岗位为关闭状态,则实际删除打招呼任务
                    if (jobStatus == 0) {
                        Integer deleted = amChatbotGreetTaskService.deleteByAccountIdAndPositionId(bossId, amPosition.getId());
                        log.info("amChatbotGreetTaskService update deleted={},bossId={},amPosition={}", deleted, bossId, amPosition);
                    }
                } catch (Exception e) {
                    log.error("savePosition异常 bossId={},platFormId={},i={}", bossId, platForm, i, e);
                }
            }

            LambdaUpdateWrapper<AmPosition> deleteQueryWrapper = new LambdaUpdateWrapper<>();
            deleteQueryWrapper.eq(AmPosition::getBossId, bossId);
            deleteQueryWrapper.notIn(AmPosition::getEncryptId, encryptIds);
            deleteQueryWrapper.set(AmPosition::getIsDeleted, 1);
            boolean updatedResult = amPositionService.update(deleteQueryWrapper);
            log.info("amPositionService update result={},bossId={}", updatedResult,bossId);
            boolean result = amZpLocalAccoutsService.updateById(amZpLocalAccouts);

            // 查询已经删除的岗位id 用于删除打招呼任务
            List<AmPosition> amPositions = amPositionService.lambdaQuery().eq(AmPosition::getBossId, bossId).eq(AmPosition::getIsDeleted, 1).list();
            for (AmPosition amPosition : amPositions) {
                Integer deleted = amChatbotGreetTaskService.deleteByAccountIdAndPositionId(bossId, amPosition.getId());
                log.info("amChatbotGreetTaskService update deleted={},bossId={},amPosition={}", deleted, bossId, amPosition);
            }

            log.info("amZpLocalAccoutsService update result={},amZpLocalAccouts={}", result, amZpLocalAccouts);
        } catch (Exception e) {
            log.error("savePosition异常 bossId={},platFormId={}", bossId, platForm, e);
        }
    }


    private void greetHandle(String platform,AmClientTasks tasksServiceOne, String taskId, String bossId, JSONObject finishTaskReqData) {
        try {
            // 提取简历信息
            JSONArray resumes = finishTaskReqData.getJSONArray("user_resumes");
            if (Objects.isNull(resumes) || resumes.isEmpty()) {
                log.error("greetHandle resumes is null,bossId={}", bossId);
                return;
            }

            //查询账号信息
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                log.error("amZpLocalAccoutsService getById is null,bossId={}", bossId);
                return;
            }

            // 开始处理打招呼的简历数据
            for (int i = 0; i < resumes.size(); i++) {
                // 开始提取简历数据, 异常捕捉,让流程继续下去
                try {
                    //开始提取简历数据
                    JSONObject resumeObject = resumes.getJSONObject(i);
                    AmResume amResume = dealAmResume(platform,amZpLocalAccouts, resumeObject);

                    //查看账号是否开启打招呼
                    LambdaQueryWrapper<AmChatbotGreetConfig> greetConfigQueryWrapper = new LambdaQueryWrapper<>();
                    greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getAccountId, amZpLocalAccouts.getId());
                    AmChatbotGreetConfig one = amChatbotGreetConfigService.getOne(greetConfigQueryWrapper, false);
                    if (Objects.isNull(one) || one.getIsGreetOn() == 0) {
                        log.info("greetHandle isGreetOn is 0,bossId={},resume={}", bossId, resumes.get(i));
                        return;
                    }

                    //提取任务里面的打招呼任务id, 目的是为了获取岗位数据
                    JSONObject jsonObject = JSONObject.parseObject(tasksServiceOne.getData());
                    if (!jsonObject.containsKey("greetId")) {
                        log.info("greetHandle greetId is null,bossId={},resume={}", bossId, resumes.get(i));
                        return;
                    }
                    //保存打招呼任务结果
                    String greetId = jsonObject.get("greetId").toString();
                    AmChatbotGreetResult amChatbotGreetResult = new AmChatbotGreetResult();
                    amChatbotGreetResult.setRechatItem(0);
                    amChatbotGreetResult.setSuccess(1);
                    amChatbotGreetResult.setAccountId(bossId);
                    amChatbotGreetResult.setCreateTime(LocalDateTime.now());
                    amChatbotGreetResult.setTaskId(Integer.parseInt(greetId));
                    amChatbotGreetResult.setUserId(amResume.getUid());
                    boolean saveResult = amChatbotGreetResultService.save(amChatbotGreetResult);

                    if (saveResult) {
                        // 生成聊天记录
                        AmChatMessage amChatMessage = new AmChatMessage();
                        amChatMessage.setConversationId(amChatbotGreetResult.getAccountId() + "_" + amResume.getUid());
                        amChatMessage.setUserId(Long.parseLong(amZpLocalAccouts.getExtBossId()));
                        amChatMessage.setRole(AIRoleEnum.ASSISTANT.getRoleName());
                        amChatMessage.setType(-1);
                        amChatMessage.setContent("你好");
                        amChatMessage.setCreateTime(LocalDateTime.now());
                        boolean save = amChatMessageService.save(amChatMessage);
                        log.info("生成聊天记录结果 amChatMessage={} result={}", JSONObject.toJSONString(amChatMessage), save);
                    }

                    //查询打招呼任务数据
                    AmChatbotGreetTask amChatbotGreetTask = amChatbotGreetTaskService.getById(greetId);
                    if (Objects.isNull(amChatbotGreetTask)) {
                        log.info("greetHandle amChatbotGreetTask is null,bossId={},resume={}", bossId, resumes.get(i));
                        return;
                    }

                    //提取岗位id, 获取岗位数据
                    Integer positionId = amChatbotGreetTask.getPositionId();
                    AmPosition amPosition = amPositionService.getById(positionId);
                    log.info("amPositionService getById amPosition={}", amPosition);

                    // 如果岗位不为空,则更新简历的岗位名称
                    if (Objects.nonNull(amPosition)) {
                        amResume.setPostId(amPosition.getId());
                        amResume.setPosition(amPosition.getName());
                        boolean result = amResumeService.updateById(amResume);
                        log.info("amResumeService update result={},amResume={}", result, amResume);
                    }
                    log.info("amChatbotGreetResultService save result={},amChatbotGreetResult={}", saveResult, amChatbotGreetResult);

                    /**
                     * 1、更新打招呼任务结果状态
                     * 3、生成复聊任务, 如果存在复聊方案
                     */
                    AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(new LambdaQueryWrapper<AmChatbotPositionOption>().eq(AmChatbotPositionOption::getAccountId, amZpLocalAccouts.getId()).eq(AmChatbotPositionOption::getPositionId, positionId), false);
                    if (Objects.isNull(amChatbotPositionOption)) {
                        log.info("复聊任务处理开始, 账号:{}, 未找到对应的职位", amZpLocalAccouts.getId());
                        continue;
                    }
                    // 查询第一天的复聊任务
                    List<AmChatbotOptionsItems> amChatbotOptionsItems = amChatbotOptionsItemsService.lambdaQuery().eq(AmChatbotOptionsItems::getOptionId, amChatbotPositionOption.getRechatOptionId()).eq(AmChatbotOptionsItems::getDayNum, 1).list();
                    if (Objects.isNull(amChatbotOptionsItems) || amChatbotOptionsItems.isEmpty()) {
                        log.info("复聊任务处理开始, 账号:{}, 未找到对应的复聊方案", amZpLocalAccouts.getId());
                        continue;
                    }

                    for (AmChatbotOptionsItems amChatbotOptionsItem : amChatbotOptionsItems) {
                        // 处理复聊任务, 存入队列里面, 用于定时任务处理
                        amChatbotGreetResult.setRechatItem(amChatbotOptionsItem.getId());
                        amChatbotGreetResult.setTaskId(amChatbotGreetTask.getId());
                        amChatbotGreetResultService.updateById(amChatbotGreetResult);
                        Long operateTime = System.currentTimeMillis() + Integer.parseInt(amChatbotOptionsItem.getExecTime())* 1000L;
                        Long zadd = jedisClient.zadd(RedisKyeConstant.AmChatBotReChatTask, operateTime, JSONObject.toJSONString(amChatbotGreetResult));
                        log.info("复聊任务处理开始, 账号:{}, 复聊任务添加结果:{}", amZpLocalAccouts.getId(), zadd);
                    }
                } catch (Exception e) {
                    log.error("greetHandle resume error,bossId={},resume={}", bossId, resumes.get(i), e);
                }
            }
        } catch (Exception e) {
            log.error("greetHandle异常 bossId={},finishTaskReqData={}", bossId, finishTaskReqData, e);
        }
    }

    private void switchJobState(String taskId, String bossId, JSONObject finishTaskReqData) {
        try {
            String encryptId = finishTaskReqData.get("encryptId").toString();
            Object state = finishTaskReqData.get("state");
            if (StringUtils.isBlank(encryptId)) {
                return;
            }
            LambdaQueryWrapper<AmPosition> positionQueryWrapper = new LambdaQueryWrapper<>();
            positionQueryWrapper.eq(AmPosition::getEncryptId, encryptId);
            positionQueryWrapper.eq(AmPosition::getBossId, bossId);
            AmPosition amPositionServiceOne = amPositionService.getOne(positionQueryWrapper, false);
            if (Objects.isNull(amPositionServiceOne)) {
                log.error("switchJobState amPositionServiceOne is null,taskId={},bossId={},finishTaskReqData={}", taskId, bossId, finishTaskReqData);
                return;
            }
            if (Objects.nonNull(state) && state.equals(true)) {
                amPositionServiceOne.setIsOpen(PositionStatusEnums.POSITION_OPEN.getStatus());
            } else {
                amPositionServiceOne.setIsOpen(PositionStatusEnums.POSITION_CLOSE.getStatus());
            }
            //清除更新状态
            amPositionServiceOne.setIsSyncing(0);
            boolean updateResult = amPositionService.updateById(amPositionServiceOne);
            log.info("switchJobState updateResult={},amPositionServiceOne={}", updateResult, amPositionServiceOne);
        } catch (Exception e) {
            log.error("switchJobState异常 taskId={},bossId={},finishTaskReqData={}", taskId, bossId, finishTaskReqData, e);
        }

    }

    private void dealUserAllInfoData(String platform, String taskId, AmZpLocalAccouts amZpLocalAccouts, JSONObject jsonObject) {
        // 开始提取保存用户简历信息
        // 提取拼接用户简历数据
        try {
            JSONObject resumeJSONObject = jsonObject.getJSONObject("resume");
            JSONObject searchData = resumeJSONObject.getJSONObject("search_data");
            JSONObject chatInfoJSONObject = jsonObject.getJSONObject("chat_info");
//            JSONObject geekDetailInfoJSONObject = resumeJSONObject.getJSONObject("geekDetailInfo");
//            JSONObject showExpectPositionSONObject = resumeJSONObject.getJSONObject("showExpectPosition");
//            JSONObject geekBaseInfo = geekDetailInfoJSONObject.getJSONObject("geekBaseInfo");
//            JSONObject chatData = chatInfoJSONObject.getJSONObject("data");
            Integer positionId = 0;
            if (Objects.nonNull(chatInfoJSONObject.get("toPositionId"))) {
                String toPositionId = chatInfoJSONObject.get("toPositionId").toString();
                LambdaQueryWrapper<AmPosition> positionQueryWrapper = new LambdaQueryWrapper<>();
                positionQueryWrapper.eq(AmPosition::getEncryptId, toPositionId);
                AmPosition amPositionServiceOne = amPositionService.getOne(positionQueryWrapper, false);
                if (Objects.nonNull(amPositionServiceOne)) {
                    positionId = amPositionServiceOne.getId();
                }
            }
            String userId = resumeJSONObject.get("uid").toString();
            if (StringUtils.isNotBlank(userId)) {
                QueryWrapper<AmResume> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("uid", userId);
                AmResume amResume = amResumeService.getOne(queryWrapper, false);
                if (Objects.isNull(amResume)) {
                    amResume = new AmResume();
                    amResume.setAdminId(amZpLocalAccouts.getAdminId());
                    amResume.setAccountId(amZpLocalAccouts.getId());
                    amResume.setCreateTime(LocalDateTime.now());

                    amResume.setPlatform(platform);

                    AmZpPlatforms byPlatformCode = amZpPlatformsService.getByPlatformCode(platform);
                    if (Objects.nonNull(byPlatformCode)) {
                        amResume.setPlatform(byPlatformCode.getName());
                    }

                    amResume.setZpData(resumeJSONObject.toJSONString());
                    amResume.setType(0);
                    amResume.setPostId(positionId);

                    // ---- begin 从resume search_data数据结构提取数据 ----
                    amResume.setCity(Objects.nonNull(searchData.get("city")) ? searchData.get("city").toString() : "");
                    amResume.setAge(Objects.nonNull(searchData.get("age")) ? Integer.parseInt(searchData.get("age").toString()) : 0);
                    amResume.setLowSalary(Objects.nonNull(searchData.get("lowSalary")) ? Integer.parseInt(searchData.get("lowSalary").toString()) : 0);
                    amResume.setHighSalary(Objects.nonNull(searchData.get("highSalary")) ? Integer.parseInt(searchData.get("highSalary").toString()) : 0);
                    amResume.setGender(Objects.nonNull(searchData.get("gender")) ? Integer.parseInt(searchData.get("gender").toString()) : 0);
                    amResume.setWorkYears(Objects.nonNull(searchData.get("workYears")) ? Integer.parseInt(searchData.get("workYears").toString()) : 0);
                    amResume.setExpectPosition(Objects.nonNull(searchData.get("toPosition")) ? searchData.get("toPosition").toString() : "");
                    // ---- end 从resume search_data数据结构提取数据  ----

                    // ---- begin 从resume数据结构提取数据  ----
                    amResume.setUid(Objects.nonNull(resumeJSONObject.get("uid")) ? resumeJSONObject.get("uid").toString() : "");
                    amResume.setName(Objects.nonNull(resumeJSONObject.get("name")) ? resumeJSONObject.get("name").toString() : "");
                    amResume.setAccountId(amZpLocalAccouts.getId());
                    amResume.setEmail(Objects.nonNull(resumeJSONObject.get("email")) ? resumeJSONObject.get("email").toString() : "");
                    amResume.setApplyStatus(Objects.nonNull(resumeJSONObject.get("availability")) ? resumeJSONObject.get("availability").toString() : "");
                    amResume.setAvatar(Objects.nonNull(resumeJSONObject.get("avatar")) ? resumeJSONObject.get("avatar").toString() : "");
                    amResume.setEducation(Objects.nonNull(resumeJSONObject.get("educations")) ? resumeJSONObject.getJSONArray("educations").toJSONString() : "");
                    amResume.setExperiences(Objects.nonNull(resumeJSONObject.get("work_experiences")) ? resumeJSONObject.getJSONArray("work_experiences").toJSONString() : "");
                    amResume.setProjects(Objects.nonNull(resumeJSONObject.get("projects")) ? resumeJSONObject.getJSONArray("projects").toJSONString() : "");
                    amResume.setEncryptGeekId(Objects.nonNull(resumeJSONObject.get("encryptGeekId")) ? resumeJSONObject.get("encryptGeekId").toString() : "");
                    amResume.setSkills(Objects.nonNull(resumeJSONObject.get("skills")) ? resumeJSONObject.get("skills").toString() : "");
                    // ---- end 从resume数据结构提取数据  ----

                    // ---- begin 从chat_info结构提取数据  ----
                    amResume.setPhone(Objects.nonNull(chatInfoJSONObject.get("phone")) ? chatInfoJSONObject.get("phone").toString() : "");
                    amResume.setWechat(Objects.nonNull(chatInfoJSONObject.get("weixin")) ? chatInfoJSONObject.get("weixin").toString() : "");
                    // ---- end 从chat_info结构提取数据  ----

                    boolean result = amResumeService.save(amResume);
                    log.info("dealUserAllInfoData result={},amResume={}", result, JSONObject.toJSONString(amResume));
                } else {
                    amResume.setPostId(positionId);
                    amResume.setZpData(resumeJSONObject.toJSONString());


                    // ---- begin 从resume search_data数据结构提取数据 ----
                    amResume.setCity(Objects.nonNull(searchData.get("city")) ? searchData.get("city").toString() : "");
                    amResume.setAge(Objects.nonNull(searchData.get("age")) ? Integer.parseInt(searchData.get("age").toString()) : 0);
                    amResume.setLowSalary(Objects.nonNull(searchData.get("lowSalary")) ? Integer.parseInt(searchData.get("lowSalary").toString()) : 0);
                    amResume.setHighSalary(Objects.nonNull(searchData.get("highSalary")) ? Integer.parseInt(searchData.get("highSalary").toString()) : 0);
                    amResume.setGender(Objects.nonNull(searchData.get("gender")) ? Integer.parseInt(searchData.get("gender").toString()) : 0);
                    amResume.setWorkYears(Objects.nonNull(searchData.get("workYears")) ? Integer.parseInt(searchData.get("workYears").toString()) : 0);
                    amResume.setExpectPosition(Objects.nonNull(searchData.get("toPosition")) ? searchData.get("toPosition").toString() : "");
                    // ---- end 从resume search_data数据结构提取数据  ----

                    // ---- begin 从resume数据结构提取数据  ----
                    amResume.setUid(Objects.nonNull(resumeJSONObject.get("uid")) ? resumeJSONObject.get("uid").toString() : "");
                    amResume.setName(Objects.nonNull(resumeJSONObject.get("name")) ? resumeJSONObject.get("name").toString() : "");
                    amResume.setAccountId(amZpLocalAccouts.getId());
                    amResume.setEmail(Objects.nonNull(resumeJSONObject.get("email")) ? resumeJSONObject.get("email").toString() : "");
                    amResume.setApplyStatus(Objects.nonNull(resumeJSONObject.get("availability")) ? resumeJSONObject.get("availability").toString() : "");
                    amResume.setAvatar(Objects.nonNull(resumeJSONObject.get("avatar")) ? resumeJSONObject.get("avatar").toString() : "");
                    amResume.setEducation(Objects.nonNull(resumeJSONObject.get("educations")) ? resumeJSONObject.getJSONArray("educations").toJSONString() : "");
                    amResume.setExperiences(Objects.nonNull(resumeJSONObject.get("work_experiences")) ? resumeJSONObject.getJSONArray("work_experiences").toJSONString() : "");
                    amResume.setProjects(Objects.nonNull(resumeJSONObject.get("projects")) ? resumeJSONObject.getJSONArray("projects").toJSONString() : "");
                    amResume.setEncryptGeekId(Objects.nonNull(resumeJSONObject.get("encryptGeekId")) ? resumeJSONObject.get("encryptGeekId").toString() : "");
                    amResume.setSkills(Objects.nonNull(resumeJSONObject.get("skills")) ? resumeJSONObject.get("skills").toString() : "");
                    // ---- end 从resume数据结构提取数据  ----

                    // ---- begin 从chat_info结构提取数据  ----
                    if (Objects.nonNull(chatInfoJSONObject.get("phone"))){
                        amResume.setPhone( chatInfoJSONObject.get("phone").toString());
                    }
                    if (Objects.nonNull(chatInfoJSONObject.get("weixin"))){
                        amResume.setWechat( chatInfoJSONObject.get("weixin").toString());
                    }
                    // ---- end 从chat_info结构提取数据  ----

                    boolean result = amResumeService.updateById(amResume);
                    log.info("dealUserAllInfoData update result={},amResume={}", result, JSONObject.toJSONString(amResume));
                }

                // 简历匹配
            }
        } catch (Exception e) {
            log.error("dealUserAllInfoData error,taskId={},amZpLocalAccouts={},jsonObject={}", taskId, amZpLocalAccouts, jsonObject, e);
        }

    }

    private AmResume dealAmResume(String platform,AmZpLocalAccouts amZpLocalAccouts, JSONObject resumeObject) {
        JSONObject searchData = resumeObject.getJSONObject("search_data");
        AmResume amResume = new AmResume();
        amResume.setAdminId(amZpLocalAccouts.getAdminId());
        amResume.setAccountId(amZpLocalAccouts.getId());

        // ---- begin 从resume search_data数据结构提取数据 ----
        amResume.setCity(Objects.nonNull(searchData.get("city")) ? searchData.get("city").toString() : "");
        amResume.setAge(Objects.nonNull(searchData.get("age")) ? Integer.parseInt(searchData.get("age").toString()) : 0);
        amResume.setLowSalary(Objects.nonNull(searchData.get("lowSalary")) ? Integer.parseInt(searchData.get("lowSalary").toString()) : 0);
        amResume.setHighSalary(Objects.nonNull(searchData.get("highSalary")) ? Integer.parseInt(searchData.get("highSalary").toString()) : 0);
        amResume.setGender(Objects.nonNull(searchData.get("gender")) ? Integer.parseInt(searchData.get("gender").toString()) : 0);
        amResume.setWorkYears(Objects.nonNull(searchData.get("workYears")) ? Integer.parseInt(searchData.get("workYears").toString()) : 0);
        amResume.setExpectPosition(Objects.nonNull(searchData.get("toPosition")) ? searchData.get("toPosition").toString() : "");
        // ---- end 从resume search_data数据结构提取数据 ----

        // ---- begin 从resume数据结构提取数据  ----
        amResume.setUid(Objects.nonNull(resumeObject.get("uid")) ? resumeObject.get("uid").toString() : "");
        amResume.setName(Objects.nonNull(resumeObject.get("name")) ? resumeObject.get("name").toString() : "");
        amResume.setAccountId(amZpLocalAccouts.getId());
        amResume.setEmail(Objects.nonNull(resumeObject.get("email")) ? resumeObject.get("email").toString() : "");
        amResume.setApplyStatus(Objects.nonNull(resumeObject.get("availability")) ? resumeObject.get("availability").toString() : "");
        amResume.setAvatar(Objects.nonNull(resumeObject.get("avatar")) ? resumeObject.get("avatar").toString() : "");
        amResume.setEducation(Objects.nonNull(resumeObject.get("educations")) ? resumeObject.getJSONArray("educations").toJSONString() : "");
        amResume.setExperiences(Objects.nonNull(resumeObject.get("work_experiences")) ? resumeObject.getJSONArray("work_experiences").toJSONString() : "");
        amResume.setProjects(Objects.nonNull(resumeObject.get("projects")) ? resumeObject.getJSONArray("projects").toJSONString() : "");
        amResume.setEncryptGeekId(Objects.nonNull(resumeObject.get("encryptGeekId")) ? resumeObject.get("encryptGeekId").toString() : "");
        amResume.setSkills(Objects.nonNull(resumeObject.get("skills")) ? resumeObject.get("skills").toString() : "");

        // ---- end 从resume数据结构提取数据  ----

        // 初筛
        amResume.setType(0);
        amResume.setCreateTime(LocalDateTime.now());
        amResume.setPlatform(platform);

        AmZpPlatforms byPlatformCode = amZpPlatformsService.getByPlatformCode(platform);
        if (Objects.nonNull(byPlatformCode)) {
            amResume.setPlatform(byPlatformCode.getName());
        }

        amResume.setZpData(resumeObject.toJSONString());
        // 打招呼取不到数据,先注释
//        amResume.setPhone(Objects.nonNull(geekBaseInfo.get("phone")) ? geekBaseInfo.get("phone").toString() : "");
//        amResume.setWechat(Objects.nonNull(geekBaseInfo.get("weixin")) ? geekBaseInfo.get("weixin").toString() : "");

//        amResume.setCompany(Objects.nonNull(geekBaseInfo.get("lastCompany")) ? geekBaseInfo.get("lastCompany").toString() : "");
//        amResume.setJobSalary(Objects.nonNull(geekBaseInfo.get("salaryDesc")) ? geekBaseInfo.get("salaryDesc").toString() : "");
//        amResume.setSalary(Objects.nonNull(geekBaseInfo.get("salaryDesc")) ? geekBaseInfo.get("salaryDesc").toString() : "");
        boolean result = amResumeService.save(amResume);
        log.info("amResumeService save result={},amResume={}", result, amResume);
        return amResume;
    }


    private void dealErrorTask(String taskId, String bossId, AmClientTasks amClientTasks) {
        try {
            String tasksData = amClientTasks.getData();
            JSONObject jsonObject = JSONObject.parseObject(tasksData);
            // 如果是get_all_job 和 switch_job_state
            if (amClientTasks.getTaskType().equals(ClientTaskTypeEnums.SWITCH_JOB_STATE.getType())) {
                String encryptId = jsonObject.get("encrypt_id").toString();
                LambdaUpdateWrapper<AmPosition> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(AmPosition::getEncryptId, encryptId).eq(AmPosition::getBossId,bossId).set(AmPosition::getIsSyncing, 0);
                boolean result = amPositionService.update(updateWrapper);
                log.info("dealErrorTask update result={},encryptId={}", result, encryptId);
            }
            if (amClientTasks.getTaskType().equals(ClientTaskTypeEnums.GET_ALL_JOB.getType())) {
                LambdaUpdateWrapper<AmPositionSyncTask> queryWrapper = new LambdaUpdateWrapper<>();
                queryWrapper.eq(AmPositionSyncTask::getAccountId, bossId).set(AmPositionSyncTask::getStatus, 2);
                amPositionSyncTaskService.update(queryWrapper);
            }
        } catch (Exception e) {
            log.error("处理失败任务异常 taskId={},bossId={}", taskId, bossId, e);
        }

    }


    //  todo 进行简历匹配已经扭转状态
    private void checkAmResume(AmResume amResume, AmZpLocalAccouts amZpLocalAccouts){
        //根据岗位id 查询出 筛选条件
        LambdaQueryWrapper<AmChatbotGreetCondition> positionQueryWrapper = new LambdaQueryWrapper<>();
        positionQueryWrapper.eq(AmChatbotGreetCondition::getAccountId, amZpLocalAccouts.getId());
        positionQueryWrapper.eq(AmChatbotGreetCondition::getPositionId, amResume.getPostId());
        AmChatbotGreetCondition condition = amChatbotGreetConditionService.getOne(positionQueryWrapper,false);
        if (Objects.nonNull(condition)) {
            //进行简历匹配
            // 将18-35 转化成数字
            Boolean result = false;
            String age = condition.getAge();
            if (StringUtils.isNotBlank(age) && !"不限".equals(age)) {
                String[] split = age.split("-");
                int minAge = Integer.parseInt(split[0]);
                int maxAge = Integer.parseInt(split[1]);
                int resumeAge = amResume.getAge();
                if (resumeAge < minAge || resumeAge > maxAge) {
                    // 符合年龄条件
                    result = true;
                }
            }
            if (result) {
                //进行简历匹配
                // 将18-35 转化成数字
                String workYears = condition.getExperience();
                if (StringUtils.isNotBlank(workYears) && !"不限".equals(workYears)) {
                    String[] split = workYears.split("-");
                    int minWorkYears = Integer.parseInt(split[0]);
                    int maxWorkYears = Integer.parseInt(split[1]);
                    int resumeWorkYears = amResume.getWorkYears();
                    if (resumeWorkYears < minWorkYears || resumeWorkYears > maxWorkYears) {
                        // 符合工作年限条件
                        result = true;
                    }
                }
            }

        }
    }
    public static void main(String[] args) {
//        String json = "{\"boss_id\":\"937aa1963da20a4a1ee3bd7db79e3185\",\"task_type\":\"send_message\",\"task_id\":\"2687d01d-ed5f-4d7e-8a8d-5e25e5dade7c\",\"success\":true,\"reason\":\"\",\"data\":{\"can_greet\":true,\"user_resumes\":[{\"blockDialog\":null,\"page\":null,\"pageV2\":null,\"geekQuestInfo\":null,\"geekQuestInfoV2\":null,\"jobCompetitive\":null,\"mateChatInfo\":{\"showMateChat\":false,\"innerAccountUser\":false,\"vipPaidUser\":true,\"vipChargeUser\":true,\"guideBuyUrl\":null,\"guideBzbUrl\":null,\"accountType\":0,\"source\":2,\"cooperateCompany\":false},\"notice\":{\"need\":false,\"msg\":null,\"usablePrivilegeMsg\":null,\"purchasableMsg\":null},\"securityJid\":\"ecpEgadOZiG2w-d1VpAahqkML398oVbJeuGEgkNxiKWVe_aFasP6W6wEA4ecnAk4HwddFuaowsTGdG3o7tTAMzl0YJNawEd3yEGOuuoPsxA7ZoxvQlVtekhrMVMNneZ_R-ucxX4vQn2t9lDbsyK8RT3fcDDWU051tY79DnmJcO0WCfiIZ_ejrDUZXrQyx008ZGmUL3gN2G1l7cexBMqAM-iP7BCA4dp2CaklIvEn-ToTQrX04MvQWJVN9OcPcdQ2-cNC6STzwOfa4AF0T-GLJ_YZutVfFK7egn6009WeJPzxmuxvtEd-COlfcbgGlSm8XuZ99jmXolUN2HK7Qq0cpKrxVk8SYI2V2YvPiyRAvjoBjgrF4zCOEeb0KyTX8wKziIibXAkpwWvEYNlW76QSmkRnG8ELDEF0P0D030LsYbamehUY8MYDdAL6B2htbXgOFbGJAEeqNKrKU0YKko4KLAr6-8L6aSWMif0SVbZl02q214-BRVU~\",\"hightLightManual\":1,\"geekDetailInfo\":{\"resumeSummary\":null,\"geekBaseInfo\":{\"name\":\"姜佳敏\",\"nameRareChars\":null,\"large\":\"https://img.bosszhipin.com/beijin/upload/avatar/20231001/607f1f3d68754fd05fac5d02611b849142bba3b79846861a129e160848b2214c1998e31b6a1c95e4.png.webp\",\"tiny\":\"https://img.bosszhipin.com/beijin/upload/avatar/20231001/607f1f3d68754fd05fac5d02611b849142bba3b79846861a129e160848b2214c1998e31b6a1c95e4_s.png.webp\",\"headImg\":0,\"gender\":0,\"degree\":203,\"degreeCategory\":\"本科\",\"freshGraduate\":3,\"applyStatus\":2,\"email\":null,\"workYears\":0,\"workDate8\":0,\"workYearDesc\":\"25年应届生\",\"age\":0,\"ageDesc\":\"21岁\",\"eventTime\":1736590826791,\"activeTimeDesc\":\"刚刚活跃\",\"unregister\":false,\"longitude\":0,\"latitude\":0,\"appVersion\":0,\"resumeStatus\":0,\"complete\":true,\"status\":0,\"applyStatusContent\":\"在校-考虑机会\",\"userId\":557777895,\"suid\":null,\"blur\":0,\"workEduDesc\":null,\"userHighlightList\":[],\"workYearsDesc\":\"25年应届生\",\"userDescription\":\"1.乐观、自信,敢于面对和克服困难,抗压能力好,有扎实的Python编程基础。\\n2.热爱编程语言,具备强烈责任心和团队合作精神\",\"foldedRows\":0,\"studyAbroadCert\":false,\"userDescHighlightList\":[],\"geekSource\":0,\"ageDescHighLightList\":null,\"designGreeting\":null,\"haveChattedText\":null,\"showSelectJob\":0,\"eliteGeekInfo\":{\"eliteGeek\":0,\"eliteIcon\":null,\"eliteExplainTips\":null,\"searchCardCostTips\":null},\"resumeAssistGuide\":null,\"advantages\":null,\"completeType\":0,\"interactiveHometown\":null,\"encryptGeekId\":\"07ea4d5c0ae7818f0nF93Nq6GFtV\",\"student\":true,\"preChatTips\":null,\"applyStatusDesc\":\"在校-考虑机会\",\"openMoment\":false,\"simpleCompleteGeek\":false},\"multiGeekVideoResume4BossVO\":null,\"resumeVideoInfo\":null,\"geekStatus\":0,\"showWorkExpDescFlag\":0,\"hasOverLapWorkExp\":false,\"toAnswerDetailUrl\":null,\"trickGeekQuestionAnswers\":null,\"showJobExperienceTip\":null,\"postExpData\":null,\"distanceText\":null,\"languageCertList\":null,\"authentication\":false,\"geekDzDoneWorks\":null,\"geekWorkPositionExpDescList\":null,\"nonMatchWorkExpIndex\":0,\"blueGeekCharacters\":null,\"blueGeekSkills\":null,\"geekExpPosList\":[{\"id\":1119331506,\"expectId\":1119331506,\"encryptExpId\":\"35c961d119fd1f081nV70t6-EVdQxA~~\",\"geekId\":557777895,\"expectType\":1,\"position\":10000028,\"positionName\":\"数据标注/AI训练师\",\"location\":101110100,\"locationName\":\"西安\",\"subLocation\":0,\"subLocationName\":null,\"lowSalary\":4,\"highSalary\":5,\"customPositionId\":0,\"deleted\":0,\"industryCodeList\":[],\"industryList\":[],\"addTime\":1736038905000,\"updateTime\":1736038905000,\"industryDesc\":\"行业不限\",\"salaryDesc\":\"4-5K\",\"positionCodeLv1\":10000000,\"positionCodeLv2\":10000028,\"potentialJobInterest\":null,\"recommendReason\":null,\"positionType\":0,\"positionNameHighlightList\":null},{\"id\":1119331469,\"expectId\":1119331469,\"encryptExpId\":\"ef5575e1227a9a5d1nV70t6-EVZWyw~~\",\"geekId\":557777895,\"expectType\":1,\"position\":10000015,\"positionName\":\"数据分析师\",\"location\":101110100,\"locationName\":\"西安\",\"subLocation\":0,\"subLocationName\":null,\"lowSalary\":4,\"highSalary\":5,\"customPositionId\":0,\"deleted\":0,\"industryCodeList\":[],\"industryList\":[],\"addTime\":1736038569000,\"updateTime\":1736038569000,\"industryDesc\":\"行业不限\",\"salaryDesc\":\"4-5K\",\"positionCodeLv1\":10000000,\"positionCodeLv2\":10000015,\"potentialJobInterest\":null,\"recommendReason\":null,\"positionType\":0,\"positionNameHighlightList\":null}],\"geekWorkExpList\":[],\"geekProjExpList\":[{\"name\":\"山西省金地杯数学建模竞赛\",\"url\":null,\"roleName\":\"核心成员\",\"performance\":\"\",\"startDate\":\"20230401\",\"endDate\":\"20230501\",\"projectId\":60562127,\"projDescHighlightList\":[],\"perfHighlightList\":[],\"projectDescription\":\"项目内容:基于python的物资存储和运送问题的研究针对物资分配和货车运送安排问题,建立了线性规划模型、整数规划模型,使用了最小生成树的遍历、贪心算法、暴力枚举算法,求解出可行性最强、效率最高的运输方案\\n负责工作:利用Python语言将数学模型转换为代码,论文的撰写\",\"workYearDesc\":\"1个月\",\"startDateDesc\":\"2023.04\",\"endDateDesc\":\"2023.05\",\"fastChatDialog\":null,\"startYearMonStr\":\"2023.04\",\"endYearMonStr\":\"2023.05\"}],\"geekEduExpList\":[{\"userId\":0,\"school\":\"晋中学院\",\"schoolId\":1580,\"major\":\"数据科学与大数据技术\",\"degree\":203,\"degreeName\":\"本科\",\"eduType\":1,\"startDate\":\"20210101\",\"endDate\":\"20250101\",\"eduId\":129260902,\"country\":\"\",\"tags\":[],\"schoolTags\":[],\"schoolType\":0,\"trainingAgency\":0,\"studyAbroad\":0,\"verifiedText\":\"\",\"eduDescription\":\"\",\"startDateDesc\":\"2021\",\"endDateDesc\":\"2025\",\"schNameHighLightList\":null,\"majorHighLightList\":null,\"majorRankingDesc\":\"专业前3%\",\"courseDesc\":null,\"keySubjectList\":null,\"badge\":\"https://img.bosszhipin.com/beijin/icon/28fefbf333c550dc29d23aafb8a7da9fb1792042df7d374e83c5b195df57be51.png\",\"briefIntroduce\":\"\",\"showBriefIntroduceIcon\":false,\"thesisTitle\":null,\"thesisDesc\":null,\"iconGray\":false}],\"geekTrainingExpList\":null,\"workExpCheckRes\":null,\"eduExpCheckRes\":null,\"attachCheckRes\":null,\"geekSocialContactList\":[],\"geekVolunteerExpList\":[{\"volunteerId\":2066363,\"name\":\"青春兴晋\",\"serviceLength\":\"200.0小时\",\"startDate\":202207,\"endDate\":202209,\"volunteerDescription\":\"参与大学生青春兴晋活动\",\"startYearMonthStr\":\"2022.07\",\"endYearMonthStr\":\"2022.09\"}],\"geekCertificationList\":[{\"certName\":\"大学英语四级\",\"certStatus\":0,\"highlight\":0,\"certTipText\":null},{\"certName\":\"普通话二级乙等\",\"certStatus\":0,\"highlight\":0,\"certTipText\":null},{\"certName\":\"大学英语六级\",\"certStatus\":0,\"highlight\":0,\"certTipText\":null},{\"certName\":\"C1驾驶证\",\"certStatus\":0,\"highlight\":0,\"certTipText\":null},{\"certName\":\"CET6\",\"certStatus\":0,\"highlight\":0,\"certTipText\":null},{\"certName\":\"CET4\",\"certStatus\":0,\"highlight\":0,\"certTipText\":null}],\"geekDesignWorksList\":null,\"attachmentResumeChatInfo\":null,\"speakTestResult\":null,\"geekQuestionAnswerList\":null,\"geekDoneWorkPositionList\":[],\"geekResumePictureModuleList\":[],\"geekResumeProductList\":[],\"geekClubExpList\":[{\"name\":\"核酸检测志愿服务\",\"roleName\":\"秩序岗\",\"desc\":\"维持现场秩序,引导居民排队,确保核酸检测工作高效运行,与其他志愿者共同合作,增强了问题解决能力和团\\n队合作精神。\",\"startDateStr\":\"2022.07\",\"endDateStr\":\"2022.08\"},{\"name\":\"红色筑梦之旅部门\",\"roleName\":\"策划部部长\",\"desc\":\"负责策划部日常运营管理,制定并执行部门工作计划与流程。在组织校园活动期间,合理分配任务,确保每个成员清楚了解自己的职责,使活动筹备工作有条不紊地进行。\",\"startDateStr\":\"2021.10\",\"endDateStr\":\"2022.06\"}],\"geekHandicappedInfo4BossVO\":null,\"rcdGeekLabel\":null,\"hitGeekUpperRightGray\":false,\"hitGeekProductUpperRight\":false,\"geekHonorList\":[{\"honorName\":\"山西省金地杯数学建模二等奖\"},{\"honorName\":\"青春兴晋优秀志愿者\"},{\"honorName\":\"数学建模省奖\"}],\"hitGeekWorkExpGray\":false,\"groupTitle\":null,\"groupMemberList\":null,\"geekOverseasPreference\":null,\"professionalSkill\":\"语言能力:通过CET4、CET6,有良好的英语听说读写能力,普通话二级乙等:,计算机能力:熟练掌握office办公软件;熟悉Python语言,具备一定的数据分析能力,获得荣誉:山西省金地杯数学建模二等奖、青春兴晋优秀志愿者;,C1驾驶证:熟练掌握驾驶技能。\",\"v110304\":-1,\"hideFullNameProcessed\":false,\"toast\":null,\"mbtiInfo\":null,\"enshrineGeek\":false,\"fromInterestListAnymous\":false,\"showExpectPosition\":{\"id\":1119331506,\"expectId\":1119331506,\"encryptExpId\":\"35c961d119fd1f081nV70t6-EVdQxA~~\",\"geekId\":557777895,\"expectType\":1,\"position\":10000028,\"positionName\":\"数据标注/AI训练师\",\"location\":101110100,\"locationName\":\"西安\",\"subLocation\":0,\"subLocationName\":null,\"lowSalary\":4,\"highSalary\":5,\"customPositionId\":0,\"deleted\":0,\"industryCodeList\":[],\"industryList\":[],\"addTime\":1736038905000,\"updateTime\":1736038905000,\"industryDesc\":\"行业不限\",\"salaryDesc\":\"4-5K\",\"positionCodeLv1\":10000000,\"positionCodeLv2\":10000028,\"potentialJobInterest\":null,\"recommendReason\":null,\"positionType\":0,\"positionNameHighlightList\":null},\"supportInterested\":false,\"geekWorksResume\":null,\"encryptJid\":\"b485df832a62be0803R-2NW0FFZV\"},\"hiddenResume\":0,\"geekStatus\":0,\"bossViewedGeekStyle\":true,\"rightsUseTip\":null,\"preJobViewGeekGray\":0,\"interBossSelectJob\":false,\"directCall\":{\"canUseDirectCall\":false,\"usedDirectCall\":false,\"count\":0,\"bannerDesc\":\"\",\"activityDesc\":\"\"},\"formattedCompanyGray\":0,\"queryZoomNewStyleGray\":1,\"feedbackDialog\":null,\"hunterIntentionEntrance\":{\"showEntrance\":false,\"text\":null,\"cardType\":null,\"hasIntention\":null,\"usedService\":false,\"rightCnt\":0,\"searchLimit\":false,\"chatLimit\":false},\"hunterGeekReqText\":null,\"anonymousChatItemOptions\":null,\"selectedItem\":0,\"toast\":null,\"geekRequirementEncJobId\":null,\"geekRequirementTags\":null,\"friend\":true}]}}";
//        JSONObject jsonObject = JSONObject.parseObject(json);
//        JSONObject dataObject = (JSONObject) jsonObject.get("data");
//        JSONArray JSONArray = (JSONArray) jsonObject.get("user_resumes");
//        for (Object resume : JSONArray) {
//            JSONObject resumeObject = (JSONObject) resume;
//            JSONObject geekDetailInfoJSONObject = (JSONObject) resumeObject.get("geekDetailInfo");
//            JSONObject geekBaseInfo = (JSONObject) geekDetailInfoJSONObject.get("geekBaseInfo");
//
//            System.out.println();
//        }

        String json = "[{\"school\":\"西安财经大学\",\"major\":\"大数据管理与 应用\",\"degree\":\"本科\",\"time_range\":\"2022 - 2026\",\"ranking\":\"专业前10%\",\"courses\":\"python、数据采集与清洗、社交网络与文本分析、数据库系统概论、概率论与数理统计\"}]";
        JSONArray jsonArray = JSONArray.parseArray(json);
        System.out.println(jsonArray);
//
//        AmResume amResume = new AmResume();
//        amResume.setCity(Objects.nonNull(chatData.get("city")) ? chatData.get("city").toString() : "");
//        amResume.setAge(Objects.nonNull(geekBaseInfo.get("age")) ? Integer.parseInt(geekBaseInfo.get("age").toString()) : 0);
//        amResume.setApplyStatus(Objects.nonNull(chatData.get("positionStatus")) ? chatData.get("positionStatus").toString() : "");
//        amResume.setCompany(Objects.nonNull(chatData.get("lastCompany")) ? chatData.get("lastCompany").toString() : "");
//        amResume.setAvatar(Objects.nonNull(chatData.get("avatar")) ? chatData.get("avatar").toString() : "");
//        amResume.setEducation(Objects.nonNull(chatData.get("school")) ? chatData.get("school").toString() : "");
//        amResume.setCreateTime(LocalDateTime.now());
//        amResume.setExperiences(Objects.nonNull(chatData.get("workExpList")) ? chatData.get("workExpList").toString() : "");
//        amResume.setEncryptGeekId(Objects.nonNull(geekBaseInfo.get("encryptGeekId")) ? geekBaseInfo.get("encryptGeekId").toString() : "");
//        amResume.setJobSalary(Objects.nonNull(chatData.get("salaryDesc")) ? chatData.get("salaryDesc").toString() : "");
//        amResume.setGender(Objects.nonNull(chatData.get("gender")) ? Integer.parseInt(chatData.get("gender").toString()) : 0);
//        amResume.setPlatform("BOSS直聘");
//        amResume.setWorkYear(Objects.nonNull(geekBaseInfo.get("workYears")) ? geekBaseInfo.get("workYears").toString() : "0");
//        amResume.setJobSalary(Objects.nonNull(chatData.get("salaryDesc")) ? chatData.get("salaryDesc").toString() : "");
//        amResume.setZpData(zpDataJSONObject.toJSONString());
//        amResume.setType(0);
//        amResume.setUid(Objects.nonNull(chatData.get("uid")) ? Integer.parseInt(chatData.get("uid").toString()) : 0);
//        amResume.setSalary(Objects.nonNull(chatData.get("salaryDesc")) ? chatData.get("salaryDesc").toString() : "");
//        amResume.setPosition(Objects.nonNull(chatData.get("toPosition")) ? chatData.get("toPosition").toString() : "");
//        amResume.setName(Objects.nonNull(chatData.get("name")) ? chatData.get("name").toString() : "");
//        System.out.println(JSONObject.toJSONString(amResume));
//
//        // 模拟从数据库返回的时间字符串
//        String dbTime = "09:00";
//
//        // 将数据库返回的时间字符串转换为 LocalTime 对象
//        LocalTime targetTime = LocalTime.parse(dbTime);
//
//
//        // 获取当前日期
//        LocalDate today = LocalDate.now();
//
//        // 将日期和时间组合成 LocalDateTime 对象
//        LocalDateTime targetDateTime = LocalDateTime.of(today, targetTime);
//
//        Long localDateTimeToTimestamp = DateUtils.convertLocalDateTimeToTimestamp(targetDateTime);
//        // 获取当前时间
//        LocalTime now = LocalTime.now();
//        System.out.println("当前时间: " + now);
//        System.out.println(now);
    }


}
