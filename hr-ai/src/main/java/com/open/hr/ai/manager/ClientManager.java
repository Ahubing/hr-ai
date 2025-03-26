package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.hr.ai.bean.req.ClientBossNewMessageReq;
import com.open.hr.ai.bean.req.ClientFinishTaskReq;
import com.open.hr.ai.bean.req.ClientQrCodeReq;
import com.open.hr.ai.bean.vo.AmGreetConditionVo;
import com.open.hr.ai.bean.vo.SlackOffVo;
import com.open.hr.ai.constant.*;
import com.open.hr.ai.convert.AmChatBotGreetNewConditionConvert;
import com.open.hr.ai.processor.BossNewMessageProcessor;
import com.open.hr.ai.util.AmClientTaskUtil;
import com.open.hr.ai.util.AmResumeFilterUtil;
import com.open.hr.ai.util.DealUserFirstSendMessageUtil;
import com.open.hr.ai.util.TimeToDecimalConverter;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private AmPositionPostServiceImpl positionPostService;

    @Resource
    private AmPositionSyncTaskServiceImpl amPositionSyncTaskService;
    @Resource
    private AmResumeServiceImpl amResumeService;
    @Resource
    private AmAdminServiceImpl amAdminService;

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
    private AmNewMaskServiceImpl amNewMaskService;

    @Resource
    private AmClientTaskUtil amClientTaskUtil;

    @Resource
    private AmChatbotGreetConditionNewServiceImpl amChatbotGreetConditionNewService;

    @Resource
    private JedisClientImpl jedisClient;

    @Autowired
    private List<BossNewMessageProcessor> bossNewMessageProcessors;

    @Resource
    private DealUserFirstSendMessageUtil dealUserFirstSendMessageUtil;


    private  LocalDateTime emptyTimestamp = LocalDateTime.of(1970, 1, 1, 0, 0, 0); // 特定的空值标识



    public ResultVO connectClient(String platform,String bossId, String connectId) {
        try {

            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail(404, "boss_id不存在");
            }
            if (AmLocalAccountStatusEnums.FREE.getStatus().equals(amZpLocalAccouts.getState())) {
                // 规定超过25秒就认定下线
                if (Objects.nonNull(amZpLocalAccouts.getUpdateTime()) && System.currentTimeMillis() - DateUtils.convertLocalDateTimeToTimestamp(amZpLocalAccouts.getUpdateTime()) < 3 * 60 * 1000) {
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
            LambdaQueryWrapper<AmClientTasks> lambdaQueryWrapper = new QueryWrapper<AmClientTasks>().lambda();
            lambdaQueryWrapper.eq(AmClientTasks::getBossId, bossId);
            lambdaQueryWrapper.eq(AmClientTasks::getTaskType, ClientTaskTypeEnums.GET_ALL_JOB.getType());
            lambdaQueryWrapper.le(AmClientTasks::getStatus, AmClientTaskStatusEnums.START.getStatus());
            int count = amClientTasksService.count(lambdaQueryWrapper);
            if (count > 0) {
                log.info("账号:{} 存在未完成的任务", bossId);
                return ResultVO.success();
            }
            if (amZpLocalAccouts.getLogined() == 0){
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

            }
            amZpLocalAccouts.setLogined(1);
            amZpLocalAccoutsService.updateById(amZpLocalAccouts);
            return ResultVO.success();
        } catch (Exception e) {
            log.error("客户端登录异常 bossId={},connectId={},extBossId={}", bossId, connectId, extBossId, e);
        }
        return ResultVO.fail(409, "登录失败");
    }


    public ResultVO queryAttachmentResume(String platform,String bossId, String connectId, String userId) {
        try {
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail(404, "boss_id不存在");
            }
            HashMap<String, Object> map = new HashMap<>();

            LambdaQueryWrapper<AmResume> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmResume::getUid, userId);
            queryWrapper.eq(AmResume::getAccountId, bossId);
            AmResume one = amResumeService.getOne(queryWrapper, false);
            if (Objects.isNull(one)) {
                map.put("exist",false);
                return ResultVO.success(map);
            }
            if (StringUtils.isNotBlank(one.getAttachmentResume())){
                map.put("exist",true);
            }else {
                map.put("exist",false);
            }
            return ResultVO.success(map);
        } catch (Exception e) {
            log.error("客户查询异常 bossId={},connectId={}", bossId, connectId, e);
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
                log.info("updateClientStatus connectId={},amZpLocalAccouts.getBrowserId()={}",connectId,amZpLocalAccouts.getBrowserId());
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
                if (Objects.nonNull(amChatbotGreetConfig.getLastCannotGreetTime())) {
                    // 判断是否小于今天的凌晨
                    if (amChatbotGreetConfig.getLastCannotGreetTime().isBefore(LocalDate.now().atStartOfDay())) {
                        jsonObject.put("canGreet",true);
                    }else {
                        jsonObject.put("canGreet",false);
                    }
                }else {
                    jsonObject.put("canGreet",true);
                }
            }


            Long adminId = amZpLocalAccouts.getAdminId();
            AmAdmin admin = amAdminService.getById(adminId);
            String slackOff = admin.getSlackOff();
            SlackOffVo slackOffVo = JSONObject.parseObject(slackOff, SlackOffVo.class);
            TimeToDecimalConverter.convertSlack(slackOffVo,jsonObject);
            // 查询返回该boss_id用户招聘中且关联好ai的职位数据。
            LambdaQueryWrapper<AmChatbotPositionOption> positionQueryWrapper = new LambdaQueryWrapper<>();
            positionQueryWrapper.eq(AmChatbotPositionOption::getAccountId, bossId);
            //查询aiAssitantId 不为0
            positionQueryWrapper.ne(AmChatbotPositionOption::getAmMaskId, 0);
            List<AmChatbotPositionOption> list = amChatbotPositionOptionService.list(positionQueryWrapper);
            // 提取出list id
            if (Objects.nonNull(list) && !list.isEmpty()){
                List<Integer> positionIds = list.stream().map(AmChatbotPositionOption::getPositionId).collect(Collectors.toList());
                LambdaQueryWrapper<AmPosition> positionLambdaQueryWrapper = new LambdaQueryWrapper<>();
                positionLambdaQueryWrapper.in(AmPosition::getId,positionIds);
                List<AmPosition> amPositions = amPositionService.list(positionLambdaQueryWrapper);
                //   "configuredAIPosition":[{ //返回该boss_id用户招聘中且关联好ai的职位数据。
                //            "id":"",
                //            "name":""
                //        }]

                JSONArray jsonArray = new JSONArray();
                for (AmPosition amPosition : amPositions) {
                    // 未开放和删除的不处理
                    if (amPosition.getIsOpen() == 0 || amPosition.getIsDeleted() == 1) continue;
                    JSONObject position = new JSONObject();
                    position.put("id",amPosition.getEncryptId());
                    position.put("name",amPosition.getName());
                    jsonArray.add(position);
                }
                jsonObject.put("configuredAIPosition",jsonArray);
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
                log.info("loginQrCodeSave connectId={},amZpLocalAccouts.getBrowserId()={}",connectId,amZpLocalAccouts.getBrowserId());
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
                log.info("getClientTask connectId={},amZpLocalAccouts.getBrowserId()={}",connectId,amZpLocalAccouts.getBrowserId());
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
                log.info("bossNewMessage connectId={},amZpLocalAccouts.getBrowserId()={}",connectId,amZpLocalAccouts.getBrowserId());
                return ResultVO.fail(401, "connect_id 不一致");
            }

            AmChatbotGreetResult resultServiceOne = amChatbotGreetResultService.getOne(new QueryWrapper<AmChatbotGreetResult>().eq("account_id", bossId).eq("user_id", req.getUser_id()).eq("success", 1), false);
            if (Objects.nonNull(resultServiceOne)) {
                resultServiceOne.setSuccess(2);
                amChatbotGreetResultService.updateById(resultServiceOne);
            }

            AmResume amResume = new AmResume();
            AtomicInteger statusCode = new AtomicInteger(0);
            for (BossNewMessageProcessor bossNewMessageProcessor : bossNewMessageProcessors) {
                bossNewMessageProcessor.dealBossNewMessage(statusCode,platform,amResume, amZpLocalAccouts, req);
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
                log.info("finishClientTask connectId={},amZpLocalAccouts.getBrowserId()={}",connectId,amZpLocalAccouts.getBrowserId());
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
                    dealErrorGreetTask(bossId,tasksServiceOne);
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
                case "send_message":
                    sendMessage(taskId,tasksServiceOne, bossId, data);
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
            String sectionName = amPositionSection.getName();

            //查询岗位信息
            LambdaQueryWrapper<AmPositionPost> postQueryWrapper = new LambdaQueryWrapper<>();
            postQueryWrapper.eq(AmPositionPost::getSectionId, sectionId);
            AmPositionPost amPositionPost = positionPostService.getOne(postQueryWrapper, false);
            if (Objects.isNull(amPositionPost)) {
                amPositionPost = new AmPositionPost();
                amPositionPost.setName("默认");
                amPositionPost.setSectionId(sectionId);
                positionPostService.save(amPositionPost);
            }
            //岗位信息
            Integer positionPostId = amPositionPost.getId();
            String postName = amPositionPost.getName();

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
                    Integer positionId = 0;
                    if (Objects.nonNull(amPosition)) {
                        positionId = amPosition.getId();
                        amPosition.setEncryptId(encryptId);
                        amPosition.setIsOpen(jobStatus);
                        amPosition.setName(jobName);
                        amPosition.setExtendParams(jobData.toJSONString());
                        amPosition.setCity(jobData.getString("locationName"));
                        amPositionService.updateById(amPosition);
                    } else {
                        AmPosition newAmPosition = new AmPosition();
                        newAmPosition.setAdminId(amZpLocalAccouts.getAdminId());
                        newAmPosition.setName(jobName);
                        newAmPosition.setPostId(positionPostId);
                        newAmPosition.setBossId(bossId);
                        newAmPosition.setUid(0);
                        newAmPosition.setChannel(amZpPlatforms.getId());
                        newAmPosition.setEncryptId(encryptId);
                        newAmPosition.setIsOpen(jobStatus);
                        newAmPosition.setCreateTime(LocalDateTime.now());
                        newAmPosition.setExtendParams(jobData.toJSONString());
                        newAmPosition.setCity(jobData.getString("locationName"));
                        boolean saveResult = amPositionService.save(newAmPosition);
                        positionId = newAmPosition.getId();
                        log.info("amPositionService save result={}, amPosition={}", saveResult, newAmPosition);
                    }
                    // 如果岗位为关闭状态,则实际删除打招呼任务
                    if (jobStatus == 0) {
                        Integer deleted = amChatbotGreetTaskService.deleteByAccountIdAndPositionId(bossId,positionId);
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


    private void greetHandle(String platform, AmClientTasks tasksServiceOne, String taskId, String bossId, JSONObject finishTaskReqData) {
        try {
            boolean canGreet = false;
            if (finishTaskReqData.containsKey("can_greet")) {
                canGreet = finishTaskReqData.getBoolean("can_greet");
                if (!canGreet) {
                    LambdaQueryWrapper<AmChatbotGreetConfig> greetConfigQueryWrapper = new LambdaQueryWrapper<>();
                    greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getAccountId, bossId);
                    AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(greetConfigQueryWrapper, false);
                    if (Objects.nonNull(amChatbotGreetConfig)) {
                        amChatbotGreetConfig.setLastCannotGreetTime(LocalDateTime.now());
                        amChatbotGreetConfigService.updateById(amChatbotGreetConfig);
                    }
                    tasksServiceOne.setStatus(AmClientTaskStatusEnums.FAILURE.getStatus());
                }
            }
            //查询账号信息
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
            if (Objects.isNull(amZpLocalAccouts)) {
                log.error("amZpLocalAccoutsService getById is null,bossId={}", bossId);
                return;
            }

            //提取任务里面的打招呼任务id, 目的是为了获取岗位数据
            JSONObject jsonObject = JSONObject.parseObject(tasksServiceOne.getData());
            if (!jsonObject.containsKey("greetId")) {
                log.info("greetHandle greetId is null,bossId={}", bossId);
                return;
            }

            //保存打招呼任务结果
            String greetId = jsonObject.get("greetId").toString();

            //查询打招呼任务数据
            AmChatbotGreetTask amChatbotGreetTask = amChatbotGreetTaskService.getById(greetId);
            if (Objects.isNull(amChatbotGreetTask)) {
                log.info("greetHandle amChatbotGreetTask is null,bossId={},greetId={}", bossId, greetId);
                return;
            }
            // 增加打招呼任务的执行次数统计
            Integer doneNum = amChatbotGreetTask.getDoneNum();
            // 提取简历信息
            JSONArray resumes = finishTaskReqData.getJSONArray("user_resumes");
            if (Objects.isNull(resumes) || resumes.isEmpty()) {
                if (doneNum >= amChatbotGreetTask.getTaskNum()) {
                    amChatbotGreetTask.setStatus(2);
                    log.info("greetHandle 任务已完成 taskId={}", tasksServiceOne.getId());
                } else {
                    // 需要继续任务
                    String data = tasksServiceOne.getData();
                    JSONObject.parseObject(data).put("times", amChatbotGreetTask.getTaskNum() - doneNum);
                    tasksServiceOne.setStatus(AmClientTaskStatusEnums.START.getStatus());
                    log.info("greetHandle 已完成 {},继续打招呼 tasksServiceOne={}", doneNum, tasksServiceOne);
                }
                boolean result = amClientTasksService.updateById(tasksServiceOne);
                log.error("greetHandle resumes is null,bossId={} updateResult={}", bossId, result);
                return;
            }

            Integer conditionsId = amChatbotGreetTask.getConditionsId();
            AmChatbotGreetConditionNew chatbotGreetConditionNew = amChatbotGreetConditionNewService.getById(conditionsId);

            // 开始处理打招呼的简历数据
            for (int i = 0; i < resumes.size(); i++) {
                // 开始提取简历数据, 异常捕捉,让流程继续下去
                try {
                    //开始提取简历数据
                    JSONObject resumeObject = resumes.getJSONObject(i);
                    doneNum++;
                    AmResume amResume = dealAmResume(platform, amZpLocalAccouts, resumeObject);
                    if (Objects.isNull(amResume)) {
                        continue;
                    }
                    Boolean filterAmResumeResult = innerFilterAmResume(chatbotGreetConditionNew, amResume, false);
                    if (!filterAmResumeResult) {
                        log.info("greetHandle filterAmResumeResult is true,bossId={},resume={}", bossId, resumes.get(i));
                        continue;
                    }
                    //查看账号是否开启打招呼
                    LambdaQueryWrapper<AmChatbotGreetConfig> greetConfigQueryWrapper = new LambdaQueryWrapper<>();
                    greetConfigQueryWrapper.eq(AmChatbotGreetConfig::getAccountId, amZpLocalAccouts.getId());
                    AmChatbotGreetConfig one = amChatbotGreetConfigService.getOne(greetConfigQueryWrapper, false);
                    if (Objects.isNull(one) || one.getIsGreetOn() == 0) {
                        log.info("AmChatbotGreetConfig isGreetOn is 0,bossId={},resume={}", bossId, resumes.get(i));
                        return;
                    }


                    AmChatbotGreetResult amChatbotGreetResult = new AmChatbotGreetResult();
                    amChatbotGreetResult.setRechatItem(0);
                    amChatbotGreetResult.setSuccess(1);
                    amChatbotGreetResult.setAccountId(bossId);
                    amChatbotGreetResult.setCreateTime(LocalDateTime.now());
                    amChatbotGreetResult.setTaskId(Integer.parseInt(greetId));
                    amChatbotGreetResult.setUserId(amResume.getUid());
                    boolean saveResult = amChatbotGreetResultService.save(amChatbotGreetResult);

                    //提取岗位id, 获取岗位数据
                    Integer positionId = amChatbotGreetTask.getPositionId();
                    AmPosition amPosition = amPositionService.getById(positionId);
                    log.info("amPositionService getById amPosition={}", amPosition);

                    // 如果岗位不为空,则更新简历的岗位名称
                    if (Objects.nonNull(amPosition)) {
                        amResume.setPostId(amPosition.getId());
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
                        Long operateTime = System.currentTimeMillis() + Integer.parseInt(amChatbotOptionsItem.getExecTime()) * 1000L;
                        Long zadd = jedisClient.zadd(RedisKyeConstant.AmChatBotReChatTask, operateTime, JSONObject.toJSONString(amChatbotGreetResult));
                        log.info("复聊任务处理开始, 账号:{}, 复聊任务添加结果:{}", amZpLocalAccouts.getId(), zadd);
                    }
                } catch (Exception e) {
                    log.error("greetHandle resume error,bossId={},resume={}", bossId, resumes.get(i), e);
                }
            }
            amChatbotGreetTask.setDoneNum(doneNum);
            if (doneNum >= amChatbotGreetTask.getTaskNum()) {
                amChatbotGreetTask.setStatus(2);
                log.info("greetHandle 任务已完成 taskId={}", tasksServiceOne.getId());
            } else {
                // 需要继续任务
                String data = tasksServiceOne.getData();
                JSONObject.parseObject(data).put("times", amChatbotGreetTask.getTaskNum() - doneNum);
                tasksServiceOne.setStatus(AmClientTaskStatusEnums.START.getStatus());
                log.info("greetHandle 已完成 {},继续打招呼 tasksServiceOne={}", doneNum, tasksServiceOne);
            }
            boolean result = amClientTasksService.updateById(tasksServiceOne);
            boolean greetTaskResult = amChatbotGreetTaskService.updateById(amChatbotGreetTask);
            log.info("greetHandle amClientTasksService update result={},greetTaskResult={},tasksServiceOne={},amChatbotGreetTask={}", result, greetTaskResult,tasksServiceOne, amChatbotGreetTask);
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
            if (Objects.isNull(jsonObject)){
                boolean result = amClientTasksService.removeById(taskId);
                log.info("dealUserAllInfoData jsonObject is null,taskId={},result={}",taskId,result);
                return;
            }
            JSONObject resumeJSONObject = jsonObject.getJSONObject("resume");
            JSONObject searchData = resumeJSONObject.getJSONObject("search_data");
            JSONObject chatInfoJSONObject = jsonObject.getJSONObject("chat_info");
//            JSONObject geekDetailInfoJSONObject = resumeJSONObject.getJSONObject("geekDetailInfo");
//            JSONObject showExpectPositionSONObject = resumeJSONObject.getJSONObject("showExpectPosition");
//            JSONObject geekBaseInfo = geekDetailInfoJSONObject.getJSONObject("geekBaseInfo");
//            JSONObject chatData = chatInfoJSONObject.getJSONObject("data");
            Integer postId = 0;
            if (Objects.nonNull(chatInfoJSONObject.get("toPositionId"))) {
                String toPositionId = chatInfoJSONObject.get("toPositionId").toString();
                LambdaQueryWrapper<AmPosition> positionQueryWrapper = new LambdaQueryWrapper<>();
                positionQueryWrapper.eq(AmPosition::getEncryptId, toPositionId);
                positionQueryWrapper.eq(AmPosition::getBossId, amZpLocalAccouts.getId());
                AmPosition amPositionServiceOne = amPositionService.getOne(positionQueryWrapper, false);
                if (Objects.nonNull(amPositionServiceOne)) {
                    postId = amPositionServiceOne.getId();
                }
            }

            String userId = resumeJSONObject.get("uid").toString();
            if (StringUtils.isNotBlank(userId)) {
                LambdaQueryWrapper<AmResume> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AmResume::getUid, userId);
                queryWrapper.eq(AmResume::getAccountId, amZpLocalAccouts.getId());
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
                    amResumeService.updateType(amResume,false,ReviewStatusEnums.RESUME_SCREENING);
                    amResume.setPostId(postId);


                    // ---- begin 从resume search_data数据结构提取数据 ----
                    amResume.setCity(Objects.nonNull(searchData.get("city")) ? searchData.get("city").toString() : "");
                    amResume.setAge(Objects.nonNull(searchData.get("age")) ? Integer.parseInt(searchData.get("age").toString()) : 0);
                    amResume.setLowSalary(Objects.nonNull(searchData.get("lowSalary")) ? Integer.parseInt(searchData.get("lowSalary").toString()) : 0);
                    amResume.setHighSalary(Objects.nonNull(searchData.get("highSalary")) ? Integer.parseInt(searchData.get("highSalary").toString()) : 0);
                    amResume.setGender(Objects.nonNull(searchData.get("gender")) ? Integer.parseInt(searchData.get("gender").toString()) : 0);
                    amResume.setWorkYears(Objects.nonNull(searchData.get("workYears")) ? Integer.parseInt(searchData.get("workYears").toString()) : 0);
                    amResume.setExpectPosition(Objects.nonNull(searchData.get("toPosition")) ? searchData.get("toPosition").toString() : "");
                    amResume.setIsStudent(Objects.nonNull(searchData.get("student")) ? searchData.get("student").equals(true) ? 1 : 0  : null);
                    amResume.setDegree(Objects.nonNull(searchData.get("degree")) ? Integer.parseInt(searchData.get("degree").toString())  : null);
                    amResume.setIntention(Objects.nonNull(searchData.get("intention")) ? Integer.parseInt(searchData.get("intention").toString()) : null);
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
                    if (Objects.nonNull(chatInfoJSONObject.get("type"))) {
                        ReviewStatusEnums statusEnums = ReviewStatusEnums.getEnumByStatus(Integer.parseInt(chatInfoJSONObject.get("type").toString()));
                        amResumeService.updateType(amResume, false, statusEnums);
                    }
                    // ---- end 从chat_info结构提取数据  ----

                    boolean result = amResumeService.save(amResume);
                    log.info("dealUserAllInfoData result={},amResume={}", result, JSONObject.toJSONString(amResume));
                }
                else {
                    amResume.setPostId(postId);
                    // 通过chat_info检测到职位变动要重新request_info在线简历，并清空聊天记录。然后再去发送消息
//                    if (!Objects.equals(positionId, amResume.getPostId())){
//                        // 重新发起请求
//                        amClientTaskUtil.buildRequestTask(amZpLocalAccouts, Integer.parseInt(amResume.getUid()), amResume,false);
//                        // 清空聊天记录
//                        LambdaQueryWrapper<AmChatMessage> amChatMessageLambdaQueryWrapper = new LambdaQueryWrapper<>();
//                        String conversationId = amZpLocalAccouts.getId() + "_" + amResume.getUid();
//                        amChatMessageLambdaQueryWrapper.eq(AmChatMessage::getConversationId, conversationId);
//                        boolean remove = amChatMessageService.remove(amChatMessageLambdaQueryWrapper);
//                        log.info("dealUserAllInfoData remove amChatMessage result={},conversationId={}", remove, conversationId);
//                    }
                    amResume.setZpData(resumeJSONObject.toJSONString());


                    // ---- begin 从resume search_data数据结构提取数据 ----
                    amResume.setCity(Objects.nonNull(searchData.get("city")) ? searchData.get("city").toString() : "");
                    amResume.setAge(Objects.nonNull(searchData.get("age")) ? Integer.parseInt(searchData.get("age").toString()) : 0);
                    amResume.setLowSalary(Objects.nonNull(searchData.get("lowSalary")) ? Integer.parseInt(searchData.get("lowSalary").toString()) : 0);
                    amResume.setHighSalary(Objects.nonNull(searchData.get("highSalary")) ? Integer.parseInt(searchData.get("highSalary").toString()) : 0);
                    amResume.setGender(Objects.nonNull(searchData.get("gender")) ? Integer.parseInt(searchData.get("gender").toString()) : 0);
                    amResume.setWorkYears(Objects.nonNull(searchData.get("workYears")) ? Integer.parseInt(searchData.get("workYears").toString()) : 0);
                    amResume.setExpectPosition(Objects.nonNull(searchData.get("toPosition")) ? searchData.get("toPosition").toString() : "");
                    amResume.setIsStudent(Objects.nonNull(searchData.get("student")) ? searchData.get("student").equals(true) ? 1 : 0  : null);
                    amResume.setDegree(Objects.nonNull(searchData.get("degree")) ? Integer.parseInt(searchData.get("degree").toString())  : null);
                    amResume.setIntention(Objects.nonNull(searchData.get("intention")) ? Integer.parseInt(searchData.get("intention").toString()) : null);
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
                    if (Objects.nonNull(chatInfoJSONObject.get("type"))) {
                        ReviewStatusEnums statusEnums = ReviewStatusEnums.getEnumByStatus(Integer.parseInt(chatInfoJSONObject.get("type").toString()));
                        amResumeService.updateType(amResume, false, statusEnums);
                    }
                    // ---- end 从chat_info结构提取数据  ----

                    boolean result = amResumeService.updateById(amResume);
                    log.info("dealUserAllInfoData update result={},amResume={}", result, JSONObject.toJSONString(amResume));
                }
                AmClientTasks amClientTasks = amClientTasksService.getById(taskId);
                if (Objects.nonNull(amClientTasks) && !Objects.equals(amResume.getType(), ReviewStatusEnums.ABANDON.getStatus())) {
                    // 处理在线简历
                    if (amClientTasks.getData().contains("[]")) {
                        dealUserFirstSendMessageUtil.dealBossNewMessage(amResume, amZpLocalAccouts);
                    }
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
        amResume.setIsStudent(Objects.nonNull(searchData.get("student")) ? searchData.get("student").equals(true) ? 1 : 0  : null);
        amResume.setDegree(Objects.nonNull(searchData.get("degree")) ? Integer.parseInt(searchData.get("degree").toString())  : null);
        amResume.setIntention(Objects.nonNull(searchData.get("intention")) ? Integer.parseInt(searchData.get("intention").toString()) : null);
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

        LambdaQueryWrapper<AmResume> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmResume::getUid, amResume.getUid());
        queryWrapper.eq(AmResume::getAccountId, amResume.getAccountId());
        queryWrapper.eq(AmResume::getExpectPosition, amResume.getExpectPosition());
        AmResume amResumeServiceOne = amResumeService.getOne(queryWrapper, false);
        if (Objects.nonNull(amResumeServiceOne)) {
            log.info("已经存在相同简历, ,amResumeId={}", amResumeServiceOne.getId());
           return null;
        }

        // 初筛
        amResumeService.updateType(amResume,false, ReviewStatusEnums.RESUME_SCREENING);
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


    private void dealErrorGreetTask(String bossId, AmClientTasks tasksServiceOne) {
        // 判断是否打招呼任务
        if (!tasksServiceOne.getTaskType().equals(ClientTaskTypeEnums.GREET.getType())) {
            return;
        }
        //提取任务里面的打招呼任务id
        log.info("dealErrorGreetTask tasksServiceOne={}", tasksServiceOne);
        JSONObject jsonObject = JSONObject.parseObject(tasksServiceOne.getData());
        if (!jsonObject.containsKey("greetId")) {
            log.info("dealErrorGreetTask greetId is null,bossId={}", bossId);
            return;
        }
        //保存打招呼任务结果
        String greetId = jsonObject.get("greetId").toString();
        //查询打招呼任务数据
        AmChatbotGreetTask amChatbotGreetTask = amChatbotGreetTaskService.getById(greetId);
        if (Objects.isNull(amChatbotGreetTask)) {
            log.info("dealErrorGreetTask amChatbotGreetTask is null,bossId={},greetId={}", bossId, greetId);
            return;
        }
        int times = amChatbotGreetTask.getTaskNum() - amChatbotGreetTask.getDoneNum();
        if (times > 0) {
            JSONObject parseObject = JSONObject.parseObject(tasksServiceOne.getData());
            if (Objects.nonNull(parseObject)) {
                JSONObject.parseObject(tasksServiceOne.getData()).put("times", times);
                tasksServiceOne.setData(JSONObject.toJSONString(parseObject));
                tasksServiceOne.setStatus(AmClientTaskStatusEnums.START.getStatus());
                tasksServiceOne.setUpdateTime(LocalDateTime.now());
            }else {
                // 参数为0,则直接删除任务
                tasksServiceOne.setStatus(AmClientTaskStatusEnums.FINISH.getStatus());
            }
            boolean result = amClientTasksService.updateById(tasksServiceOne);
            log.info("amClientTasksService update times={} result={},tasksServiceOne={}", times,result, tasksServiceOne);

        }
    }


    private void sendMessage(String taskId,AmClientTasks tasksServiceOne,  String bossId, JSONObject finishTaskReqData) {
        try {
            if (Objects.nonNull(finishTaskReqData) &&  finishTaskReqData.containsKey("type")) {
                String data = tasksServiceOne.getData();
                JSONObject jsonObject = JSONObject.parseObject(data);
                String userId = jsonObject.get("user_id").toString();
                LambdaQueryWrapper<AmResume> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AmResume::getUid, userId);
                queryWrapper.eq(AmResume::getAccountId, bossId);
                AmResume amResume = amResumeService.getOne(queryWrapper, false);
                amResumeService.updateType(amResume,false,ReviewStatusEnums.ABANDON);
                boolean result = amResumeService.updateById(amResume);
                log.info("sendMessage update result={},amResume={}", result, amResume);
            }

        } catch (Exception e) {
            log.error("deal sendMessage error taskId={},bossId={},finishTaskReqData={}", taskId, bossId, finishTaskReqData, e);
        }

    }

    public ResultVO filterAmResume(String platform,String bossId, String connectId, String encryptId,JSONObject resumeObject,Boolean isGreet) {

        AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
        if (Objects.isNull(amZpLocalAccouts)) {
            return ResultVO.fail(404, "boss_id不存在");
        }

        if (!amZpLocalAccouts.getBrowserId().equals(connectId)) {
            return ResultVO.fail(401, "connect_id 不一致");
        }

        LambdaQueryWrapper<AmPosition> positionQueryWrapper = new LambdaQueryWrapper<>();
        positionQueryWrapper.eq(AmPosition::getEncryptId, encryptId);
        positionQueryWrapper.eq(AmPosition::getBossId, bossId);
        AmPosition amPositionServiceOne = amPositionService.getOne(positionQueryWrapper, false);
        if (Objects.isNull(amPositionServiceOne)) {
            log.error("filterAmResume amPositionServiceOne is null,bossId={},encryptId={}", bossId, encryptId);
            return ResultVO.fail(404, "找不到职位");
        }

        LambdaQueryWrapper<AmChatbotGreetConditionNew> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AmChatbotGreetConditionNew::getPositionId, amPositionServiceOne.getId());
        lambdaQueryWrapper.eq(AmChatbotGreetConditionNew::getAccountId, bossId);
        AmChatbotGreetConditionNew conditionNewServiceOne = amChatbotGreetConditionNewService.getOne(lambdaQueryWrapper, false);
        if (Objects.isNull(conditionNewServiceOne)) {
            conditionNewServiceOne = amChatbotGreetConditionNewService.getById(1);
            log.error("switchJobState conditionNewServiceOne is null,bossId={},encryptId={}", bossId, encryptId);
//            return ResultVO.fail(404, "找不到筛选条件");
        }
        AmResume amResume = buildAmResume(amPositionServiceOne, amZpLocalAccouts, resumeObject);
        Boolean result = innerFilterAmResume( conditionNewServiceOne, amResume,isGreet);
        log.info("filterAmResume result={},amResume={}", result, resumeObject);
        return ResultVO.success(result);
    }



    public ResultVO filterAndSaveAmResume(String platform,String bossId, String connectId, String encryptId,JSONObject resumeObject,Boolean isGreet,String greet_task_id) {

        AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(bossId);
        if (Objects.isNull(amZpLocalAccouts)) {
            return ResultVO.fail(404, "boss_id不存在");
        }

        if (!amZpLocalAccouts.getBrowserId().equals(connectId)) {
            return ResultVO.fail(401, "connect_id 不一致");
        }

        LambdaQueryWrapper<AmPosition> positionQueryWrapper = new LambdaQueryWrapper<>();
        positionQueryWrapper.eq(AmPosition::getEncryptId, encryptId);
        positionQueryWrapper.eq(AmPosition::getBossId, bossId);
        AmPosition amPositionServiceOne = amPositionService.getOne(positionQueryWrapper, false);
        if (Objects.isNull(amPositionServiceOne)) {
            log.error("filterAndSaveAmResume amPositionServiceOne is null,bossId={},encryptId={}", bossId, encryptId);
            return ResultVO.fail(404, "找不到职位");
        }

        LambdaQueryWrapper<AmChatbotGreetConditionNew> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AmChatbotGreetConditionNew::getPositionId, amPositionServiceOne.getId());
        lambdaQueryWrapper.eq(AmChatbotGreetConditionNew::getAccountId, bossId);
        AmChatbotGreetConditionNew conditionNewServiceOne = amChatbotGreetConditionNewService.getOne(lambdaQueryWrapper, false);
        if (Objects.isNull(conditionNewServiceOne)) {
            conditionNewServiceOne = amChatbotGreetConditionNewService.getById(1);
            log.error("filterAndSaveAmResume conditionNewServiceOne is null,bossId={},encryptId={}", bossId, encryptId);
        }
        AmResume amResume = buildAmResume(amPositionServiceOne, amZpLocalAccouts, resumeObject);
        Boolean result = innerFilterAmResume( conditionNewServiceOne, amResume,isGreet);
        if (isGreet && result) {
            // 根据bossId 和uid 查询任务是否存在, 不存在则插入
            LambdaQueryWrapper<AmResume> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmResume::getUid, amResume.getUid());
            queryWrapper.eq(AmResume::getAccountId, amResume.getAccountId());
            AmResume amResumeServiceOne = amResumeService.getOne(queryWrapper, false);
            if (Objects.isNull(amResumeServiceOne)) {
                AmClientTasks amClientTasks = amClientTasksService.getById(greet_task_id);
                if (Objects.isNull(amClientTasks)) {
                    log.error("filterAndSaveAmResume amClientTasks is null,bossId={},greet_task_id={}", bossId, greet_task_id);
                    return ResultVO.fail(404, "找不到客户端任务");
                }

                //提取任务里面的打招呼任务id, 目的是为了获取岗位数据
                JSONObject jsonObject = JSONObject.parseObject(amClientTasks.getData());
                if (!jsonObject.containsKey("greetId")) {
                    log.info("filterAndSaveAmResume greetHandle greetId is null,bossId={}", bossId);
                    return ResultVO.fail(404, "找不到打招呼任务Id");
                }

                //保存打招呼任务结果
                String greetId = jsonObject.get("greetId").toString();

                //查询打招呼任务数据
                AmChatbotGreetTask amChatbotGreetTask = amChatbotGreetTaskService.getById(greetId);
                if (Objects.isNull(amChatbotGreetTask)) {
                    log.info("greetHandle amChatbotGreetTask is null,bossId={},greetId={}", bossId, greetId);
                    return ResultVO.fail(404, "找不到打招呼任务");
                }
                // 增加打招呼任务的执行次数统计
                Integer doneNum = amChatbotGreetTask.getDoneNum();
                boolean amResumeResult = amResumeService.save(amResume);
                log.info("filterAndSaveAmResume amResumeResult={},amResume={}", amResumeResult, amResume);
                doneNum++;
                amChatbotGreetTask.setDoneNum(doneNum);
                amClientTasksService.updateById(amClientTasks);
                boolean updateGreetTask = amChatbotGreetTaskService.updateById(amChatbotGreetTask);
                log.info("filterAndSaveAmResume updateGreetTask={},amChatbotGreetTask={}", updateGreetTask, amChatbotGreetTask);
                AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(new LambdaQueryWrapper<AmChatbotPositionOption>().eq(AmChatbotPositionOption::getAccountId, amZpLocalAccouts.getId()).eq(AmChatbotPositionOption::getPositionId, amPositionServiceOne.getId()), false);
                if (Objects.nonNull(amChatbotPositionOption)) {
                    Long amMaskId = amChatbotPositionOption.getAmMaskId();
                    AmNewMask amNewMask = amNewMaskService.getById(amMaskId);
                    if (Objects.nonNull(amNewMask) && StringUtils.isNotBlank(amNewMask.getGreetMessage())){
                        // 生成聊天记录
                        AmChatMessage amChatMessage = new AmChatMessage();
                        amChatMessage.setConversationId( amZpLocalAccouts.getId() + "_" + amResume.getUid());
                        amChatMessage.setUserId(Long.parseLong(amZpLocalAccouts.getExtBossId()));
                        amChatMessage.setRole(AIRoleEnum.ASSISTANT.getRoleName());
                        amChatMessage.setType(-1);
                        amChatMessage.setContent(amNewMask.getGreetMessage());
                        amChatMessage.setCreateTime(LocalDateTime.now());
                        boolean save = amChatMessageService.save(amChatMessage);
                        log.info("filterAndSaveAmResume 过滤简历, 生成聊天记录结果 amChatMessage={} result={}", JSONObject.toJSONString(amChatMessage), save);
                    }
                    // 处理复聊任务
                    AmChatbotGreetResult amChatbotGreetResult = new AmChatbotGreetResult();
                    amChatbotGreetResult.setRechatItem(0);
                    amChatbotGreetResult.setSuccess(1);
                    amChatbotGreetResult.setAccountId(bossId);
                    amChatbotGreetResult.setCreateTime(LocalDateTime.now());
                    amChatbotGreetResult.setTaskId(Integer.parseInt(greetId));
                    amChatbotGreetResult.setUserId(amResume.getUid());
                    boolean saveResult = amChatbotGreetResultService.save(amChatbotGreetResult);
                    log.info("filterAndSaveAmResume saveResult={},amChatbotGreetResult={}", saveResult, amChatbotGreetResult);
                    // 查询第一天的复聊任务
                    List<AmChatbotOptionsItems> amChatbotOptionsItems = amChatbotOptionsItemsService.lambdaQuery().eq(AmChatbotOptionsItems::getOptionId, amChatbotPositionOption.getRechatOptionId()).eq(AmChatbotOptionsItems::getDayNum, 1).list();
                    if (Objects.nonNull(amChatbotOptionsItems) &&  !amChatbotOptionsItems.isEmpty()) {
                        for (AmChatbotOptionsItems amChatbotOptionsItem : amChatbotOptionsItems) {
                            // 处理复聊任务, 存入队列里面, 用于定时任务处理
                            amChatbotGreetResult.setRechatItem(amChatbotOptionsItem.getId());
                            amChatbotGreetResult.setTaskId(amChatbotGreetTask.getId());
                            amChatbotGreetResultService.updateById(amChatbotGreetResult);
                            Long operateTime = System.currentTimeMillis() + Integer.parseInt(amChatbotOptionsItem.getExecTime()) * 1000L;
                            Long zadd = jedisClient.zadd(RedisKyeConstant.AmChatBotReChatTask, operateTime, JSONObject.toJSONString(amChatbotGreetResult));
                            log.info("复聊任务处理开始, 账号:{}, 复聊任务添加结果:{}", amZpLocalAccouts.getId(), zadd);
                        }
                    }else {
                        log.info("复聊任务处理开始, 账号:{}, 未找到对应的复聊方案", amZpLocalAccouts.getId());
                    }
                }
            }
        }
        log.info("filterAmResume result={},amResume={}", result, resumeObject);
        return ResultVO.success(result);
    }

    public Boolean innerFilterAmResume(AmChatbotGreetConditionNew conditionNewServiceOne,AmResume amResume,Boolean isGreet) {
        AmGreetConditionVo amGreetConditionVo = AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(conditionNewServiceOne);
        boolean result = AmResumeFilterUtil.filterResume(amResume, amGreetConditionVo,isGreet);
        log.info("filterAmResume result={},amResume={},amGreetConditionVo={}", result, amResume,amGreetConditionVo);
        return result;
    }

    private AmResume buildAmResume(AmPosition amPosition,AmZpLocalAccouts amZpLocalAccouts, JSONObject resumeObject) {
        JSONObject searchData = resumeObject.getJSONObject("search_data");

        AmResume amResume = new AmResume();

        amResume.setPostId(amPosition.getId());
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
        amResume.setIsStudent(Objects.nonNull(searchData.get("student")) ? searchData.get("student").equals(true) ? 1 : 0  : null);
        amResume.setDegree(Objects.nonNull(searchData.get("degree")) ? Integer.parseInt(searchData.get("degree").toString())  : null);
        amResume.setIntention(Objects.nonNull(searchData.get("intention")) ? Integer.parseInt(searchData.get("intention").toString()) : null);
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
        amResume.setType(ReviewStatusEnums.RESUME_SCREENING.getStatus());
        // ---- end 从resume数据结构提取数据  ----
//        amResumeService.updateType(amResume,false,ReviewStatusEnums.RESUME_SCREENING);
        amResume.setCreateTime(LocalDateTime.now());
        amResume.setZpData(resumeObject.toJSONString());
        return amResume;
    }



}
