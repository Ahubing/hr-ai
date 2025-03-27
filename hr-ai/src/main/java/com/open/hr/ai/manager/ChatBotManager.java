package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.mysql.hr.vo.AmGreetConditionVo;
import com.open.ai.eros.db.mysql.hr.vo.AmPositionVo;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.*;
import com.open.hr.ai.constant.*;
import com.open.hr.ai.convert.*;
import com.open.hr.ai.util.AmGreetTaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 逻辑按照php处理的, 暂时未调试
 *
 * @Date 2025/1/4 13:32
 */
@Component
@Slf4j
public class ChatBotManager {

    @Resource
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;

    @Resource
    private AmChatbotGreetResultServiceImpl amChatbotGreetResultService;
    @Resource
    private AmChatbotGreetMessagesServiceImpl amChatbotGreetMessagesService;
    @Resource
    private AmChatMessageServiceImpl amChatMessageService;

    @Resource
    private AmResumeServiceImpl amResumeService;

    @Resource
    private AmZpPlatformsServiceImpl amZpPlatformsService;

    @Resource
    private AmChatbotOptionsConfigServiceImpl amChatbotOptionsConfigService;

    @Resource
    private AmChatbotGreetConfigServiceImpl amChatbotGreetConfigService;

    @Resource
    private AmChatbotGreetConditionNewServiceImpl amChatbotGreetConditionNewService;

    @Resource
    private AmChatbotGreetTaskServiceImpl amChatbotGreetTaskService;

    @Resource
    private AmChatbotPositionOptionServiceImpl amChatbotPositionOptionService;


    @Resource
    private AmMaskServiceImpl amMaskService;


    @Resource
    private AmChatbotOptionsServiceImpl amChatbotOptionsService;

    @Resource
    private AmPositionSyncTaskServiceImpl amPositionSyncTaskService;

    @Resource
    private AmClientTasksServiceImpl amClientTasksService;
    @Resource
    private AmPositionServiceImpl amPositionService;

    @Resource
    private AmGreetTaskUtil amGreetTaskUtil;


    @Resource
    private AmNewMaskManager amNewMaskManager;

    @Resource
    private ChatBotOptionsManager chatBotOptionsManager;


    @Resource
    private JedisClientImpl jedisClient;


    public ResultVO<AmZmLocalAccountsListVo> getLocalAccounts(Long adminId) {

        try {
            AmZmLocalAccountsListVo amZmLocalAccountsListVo = new AmZmLocalAccountsListVo();
            LambdaQueryWrapper<AmZpLocalAccouts> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(AmZpLocalAccouts::getAdminId, adminId);
            lambdaQueryWrapper.eq(AmZpLocalAccouts::getStatus, 1);
            lambdaQueryWrapper.orderByAsc(AmZpLocalAccouts::getId);
            List<AmZpLocalAccouts> localAccounts = amZpLocalAccoutsService.list(lambdaQueryWrapper);
            if (CollectionUtils.isEmpty(localAccounts)) {
                return ResultVO.success();
            }

            List<AmZpLocalAccoutsVo> amZpLocalAccoutsVos = localAccounts.stream().map(AmZpLocalAccoutsConvert.I::convertAmZpLocalAccounts).collect(Collectors.toList());
            List<AmZpPlatforms> platforms = amZpPlatformsService.list();

            for (AmZpLocalAccoutsVo account : amZpLocalAccoutsVos) {
//                if (Objects.nonNull(account.getUpdateTime()) && (System.currentTimeMillis() - DateUtils.convertLocalDateTimeToTimestamp(account.getUpdateTime())) > 25 * 1000) {
//                    account.setState("offline");
//                }

//                if (Objects.isNull(account.getUpdateTime()) || ( System.currentTimeMillis() / 1000 - account.getUpdateTime().getSecond()) > 25 ) {
//                    account.setState("offline");
//                }
                for (AmZpPlatforms platform : platforms) {
                    if (platform.getId().equals(account.getPlatformId())) {
                        account.setPlatform(platform.getName());
                    }
                }
            }

            amZmLocalAccountsListVo.setLocalAccountsList(amZpLocalAccoutsVos);
            amZmLocalAccountsListVo.setPlatforms(platforms);
            amZmLocalAccountsListVo.setCitys(Arrays.asList("北京", "上海", "深圳", "广州", "杭州", "成都"));
            return ResultVO.success(amZmLocalAccountsListVo);
        } catch (Exception e) {
            log.error("getLocalAccounts error", e);
        }
        return ResultVO.fail("获取账号列表失败");
    }


    public ResultVO<List<AmZpPlatforms>> getPlatforms() {
        try {
            List<AmZpPlatforms> platforms = amZpPlatformsService.list();
            return ResultVO.success(platforms);
        } catch (Exception e) {
            log.error("getPlatforms error", e);
        }
        return ResultVO.fail("获取平台列表失败");
    }

    public ResultVO<AmZpLocalAccouts> AddAccount(AddOrUpdateAccountReq addOrUpdateAccountReq, Long adminId) {
        try {

            LambdaQueryWrapper<AmZpLocalAccouts> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmZpLocalAccouts::getAdminId, adminId);
            queryWrapper.eq(AmZpLocalAccouts::getStatus, 1);
            queryWrapper.eq(AmZpLocalAccouts::getAccount, addOrUpdateAccountReq.getAccount());
            AmZpLocalAccouts zpLocalAccouts = amZpLocalAccoutsService.getOne(queryWrapper);
            if (Objects.nonNull(zpLocalAccouts)) {
                return ResultVO.fail("账号已存在");
            }
            String uuid = UUID.randomUUID().toString();
            AmZpLocalAccouts amZpLocalAccouts = new AmZpLocalAccouts();
            amZpLocalAccouts.setId(uuid);
            amZpLocalAccouts.setAdminId(adminId);
            amZpLocalAccouts.setPlatformId(addOrUpdateAccountReq.getPlatformId());
            amZpLocalAccouts.setAccount(addOrUpdateAccountReq.getAccount());
            amZpLocalAccouts.setMobile(addOrUpdateAccountReq.getMobile());
            amZpLocalAccouts.setCity(addOrUpdateAccountReq.getCity());
            amZpLocalAccouts.setCreateTime(LocalDateTime.now());
            boolean result = amZpLocalAccoutsService.save(amZpLocalAccouts);
            return result ? ResultVO.success("添加成功") : ResultVO.fail("添加失败");
        } catch (Exception e) {
            log.error("AddAccount error", e);
        }
        return ResultVO.fail("添加账号失败");
    }


    public ResultVO<AmZpLocalAccouts> updateAccount(AddOrUpdateAccountReq addOrUpdateAccountReq, Long adminId) {
        try {

            AmZpLocalAccouts zpLocalAccouts = amZpLocalAccoutsService.getById(addOrUpdateAccountReq.getId());
            if (Objects.isNull(zpLocalAccouts)) {
                return ResultVO.fail("账号不存在");
            }
            if (!Objects.equals(zpLocalAccouts.getAdminId(), adminId)) {
                return ResultVO.fail("无权限修改");
            }

            if (Objects.nonNull(addOrUpdateAccountReq.getAccount())){
                zpLocalAccouts.setAccount(addOrUpdateAccountReq.getAccount());
            }
            if (Objects.nonNull(addOrUpdateAccountReq.getMobile())){
                zpLocalAccouts.setMobile(addOrUpdateAccountReq.getMobile());
            }
            if (Objects.nonNull(addOrUpdateAccountReq.getCity())){
                zpLocalAccouts.setCity(addOrUpdateAccountReq.getCity());
            }
            if (Objects.nonNull(addOrUpdateAccountReq.getPlatformId())){
                zpLocalAccouts.setPlatformId(addOrUpdateAccountReq.getPlatformId());
            }
            zpLocalAccouts.setUpdateTime(LocalDateTime.now());
            boolean result = amZpLocalAccoutsService.updateById(zpLocalAccouts);
            return result ? ResultVO.success("修改成功") : ResultVO.fail("修改失败");
        } catch (Exception e) {
            log.error("updateAccount error", e);
        }
        return ResultVO.fail("修改账号失败");
    }

    public ResultVO deleteAccount(String id) {
        try {
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(id);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail("账号不存在");
            }
            amZpLocalAccouts.setStatus(-1);
            boolean result = amZpLocalAccoutsService.updateById(amZpLocalAccouts);
            if (result){
                //删除绑定的复聊任务 ai 面具
                amChatbotPositionOptionService.remove(new LambdaQueryWrapper<AmChatbotPositionOption>().eq(AmChatbotPositionOption::getAccountId, id));
            }
            return result ? ResultVO.success("删除成功") : ResultVO.fail("删除失败");
        } catch (Exception e) {
            log.error("deleteAccount error", e);
        }
        return ResultVO.fail("删除账号失败");
    }

    public ResultVO<AmZpLocalAccoutsVo> getGreetByAccountId(String id) {
        try {
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(id);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail("账号不存在");
            }
            AmZpLocalAccoutsVo amZpLocalAccoutsVo = AmZpLocalAccoutsConvert.I.convertAmZpLocalAccounts(amZpLocalAccouts);
            return ResultVO.success(amZpLocalAccoutsVo);
        } catch (Exception e) {
            log.error("getGreetByAccountId error id=", e);
        }
        return ResultVO.fail("获取打招呼设置失败");
    }


    /**
     * php 迁移过来的逻辑
     * @param accountId
     * @param adminId
     * @return
     */
    public ResultVO<AmChatBotGreetConfigDataVo> getGreetConfig(String accountId, Long adminId) {
        try {
            AmChatBotGreetConfigDataVo amChatBotGreetConfigDataVo = new AmChatBotGreetConfigDataVo();
            AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(new LambdaQueryWrapper<AmChatbotGreetConfig>().eq(AmChatbotGreetConfig::getAccountId, accountId), false);
            if (Objects.isNull(amChatbotGreetConfig)) {
                amChatbotGreetConfig = new AmChatbotGreetConfig();
                amChatbotGreetConfig.setAccountId(accountId);
                amChatbotGreetConfig.setAdminId(adminId);
                amChatbotGreetConfigService.save(amChatbotGreetConfig);
            }
            List<AmChatbotGreetTask> amChatbotGreetTasks = amChatbotGreetTaskService.list(new LambdaQueryWrapper<AmChatbotGreetTask>().eq(AmChatbotGreetTask::getAccountId, accountId));
            List<AmChatbotGreetTaskVo> amChatbotGreetTaskVos = amChatbotGreetTasks.stream().map(AmChatBotGreetTaskConvert.I::convertGreetTaskVo).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(amChatbotGreetTaskVos)) {
                for (AmChatbotGreetTaskVo amChatbotGreetTaskVo : amChatbotGreetTaskVos) {
                    AmPosition amPosition = amPositionService.getById(amChatbotGreetTaskVo.getPositionId());
                    AmPositionVo amPositionVo = AmPositionConvert.I.converAmPositionVo(amPosition);
                    amChatbotGreetTaskVo.setPositionVo(amPositionVo);
                    AmChatbotGreetConditionNew amChatbotGreetCondition = amChatbotGreetConditionNewService.getOne(new LambdaQueryWrapper<AmChatbotGreetConditionNew>().eq(AmChatbotGreetConditionNew::getPositionId, amChatbotGreetTaskVo.getPositionId()), false);
                    if (Objects.nonNull(amChatbotGreetCondition)) {
                        amChatbotGreetTaskVo.setConditionVo(AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(amChatbotGreetCondition));
                    }
                }
            }
            amChatBotGreetConfigDataVo.setConfig(amChatbotGreetConfig);
            amChatBotGreetConfigDataVo.setTasks(amChatbotGreetTaskVos);

            int count = amChatbotGreetResultService.count(new LambdaQueryWrapper<AmChatbotGreetResult>().eq(AmChatbotGreetResult::getAccountId, accountId).ge(AmChatbotGreetResult::getCreateTime, LocalDate.now().atStartOfDay()));
            amChatBotGreetConfigDataVo.setToday_searched(count);

            int monthCount = amChatbotGreetResultService.count(new LambdaQueryWrapper<AmChatbotGreetResult>().eq(AmChatbotGreetResult::getAccountId, accountId).ge(AmChatbotGreetResult::getCreateTime, LocalDate.now().withDayOfMonth(1).atStartOfDay()));
            amChatBotGreetConfigDataVo.setThis_month_searched(monthCount);

            amChatBotGreetConfigDataVo.setToday_tmp_task("0/0");
            amChatBotGreetConfigDataVo.setToday_scheduled_task("0/0");
            amChatBotGreetConfigDataVo.setCurrent_time_task("0/0");
            amChatBotGreetConfigDataVo.setRechat_no_reply_amount("0/0");
            amChatBotGreetConfigDataVo.setRechat_ask_resume_amount("0/0");

            AccountDataVo accountDataVo = new AccountDataVo();
            int amResumeCount = amResumeService.count(new LambdaQueryWrapper<AmResume>().eq(AmResume::getAccountId, accountId).ge(AmResume::getCreateTime, LocalDate.now().atStartOfDay()));
            accountDataVo.setToday_resume(amResumeCount);
            int amResumeMonthCount = amResumeService.count(new LambdaQueryWrapper<AmResume>().eq(AmResume::getAccountId, accountId).ge(AmResume::getCreateTime, LocalDate.now().withDayOfMonth(1).atStartOfDay()));
            accountDataVo.setT_month_resume(amResumeMonthCount);

            int attachmentResumeCount = amResumeService.count(new LambdaQueryWrapper<AmResume>()
                    .eq(AmResume::getAccountId, accountId)
                    .ge(AmResume::getCreateTime, LocalDate.now().atStartOfDay())
                    .isNotNull(AmResume::getAttachmentResume));
            accountDataVo.setToday_attachment_resume(attachmentResumeCount);


            int attachmentResumeMonthCount = amResumeService.count(new LambdaQueryWrapper<AmResume>()
                    .eq(AmResume::getAccountId, accountId)
                    .ge(AmResume::getCreateTime, LocalDate.now().withDayOfMonth(1).atStartOfDay())
                    .isNotNull(AmResume::getAttachmentResume));
            accountDataVo.setT_month_attachment_resume(attachmentResumeMonthCount);

            List<Map<String, Object>> today_communication = amChatMessageService.listMaps(new LambdaQueryWrapper<AmChatMessage>()
                    .select(AmChatMessage::getUserId) // 选择userId字段
                    .ge(AmChatMessage::getCreateTime, LocalDate.now().atStartOfDay()) // 大于等于今天的开始时间
                    .groupBy(AmChatMessage::getUserId));
            List<Map<String, Object>> tMonth_communication = amChatMessageService.listMaps(new LambdaQueryWrapper<AmChatMessage>()
                    .select(AmChatMessage::getUserId) // 选择userId字段
                    .ge(AmChatMessage::getCreateTime, LocalDate.now().withDayOfMonth(1).atStartOfDay()) // 大于等于今天的开始时间
                    .groupBy(AmChatMessage::getUserId));
            accountDataVo.setToday_communication(today_communication.size());
            accountDataVo.setT_month_communication(tMonth_communication.size());


            int todayRechat = amChatbotGreetMessagesService.count(new LambdaQueryWrapper<AmChatbotGreetMessages>().eq(AmChatbotGreetMessages::getAccountId, accountId).ge(AmChatbotGreetMessages::getCreateTime, LocalDate.now().atStartOfDay()).eq(AmChatbotGreetMessages::getTaskType, MessageTypeEnums.rechat.getCode()));
            int tMonthRechat = amChatbotGreetMessagesService.count(new LambdaQueryWrapper<AmChatbotGreetMessages>().eq(AmChatbotGreetMessages::getAccountId, accountId).ge(AmChatbotGreetMessages::getCreateTime, LocalDate.now().withDayOfMonth(1).atStartOfDay()).eq(AmChatbotGreetMessages::getTaskType, MessageTypeEnums.rechat.getCode()));
            accountDataVo.setToday_rechat(todayRechat);
            accountDataVo.setT_month_rechat(tMonthRechat);

            LambdaQueryWrapper<AmChatbotGreetMessages> lambdaQueryWrapper = new LambdaQueryWrapper<AmChatbotGreetMessages>()
                    .eq(AmChatbotGreetMessages::getAccountId, accountId)
                    .ge(AmChatbotGreetMessages::getCreateTime, LocalDate.now().atStartOfDay())
                    .eq(AmChatbotGreetMessages::getTaskType, MessageTypeEnums.rechat.getCode())
                    .isNotNull(AmChatbotGreetMessages::getFromUid);
            // 查询出账号今日复聊的用户id , 再根据去重的用户id, 查询是否有聊天记录
            List<Integer> userIds = amChatbotGreetMessagesService.list(lambdaQueryWrapper)
                    .stream()
                    .map(AmChatbotGreetMessages::getFromUid)
                    .collect(Collectors.toList());
            accountDataVo.setToday_active(0);
            if (!userIds.isEmpty()){
                int activeCount = amChatMessageService.count(new LambdaQueryWrapper<AmChatMessage>()
                        .in(AmChatMessage::getUserId, userIds)
                        .eq(AmChatMessage::getCreateTime, LocalDate.now().atStartOfDay())
                        .select(AmChatMessage::getUserId) // 选择 userId 字段
                        .groupBy(AmChatMessage::getUserId)); // 根据 userId 分组
                accountDataVo.setToday_active(activeCount); // 将统计结果设置到 accountDataVo
            }

            lambdaQueryWrapper.ge(AmChatbotGreetMessages::getCreateTime,  LocalDate.now().withDayOfMonth(1).atStartOfDay());
            accountDataVo.setT_month_active(0);
            List<Integer> monthUserIds = amChatbotGreetMessagesService.list(lambdaQueryWrapper)
                    .stream()
                    .map(AmChatbotGreetMessages::getFromUid)
                    .collect(Collectors.toList());
            amChatBotGreetConfigDataVo.setAccountData(accountDataVo);

            if (!monthUserIds.isEmpty()){
                int activeCount = amChatMessageService.count(new LambdaQueryWrapper<AmChatMessage>()
                        .in(AmChatMessage::getUserId, monthUserIds)
                        .eq(AmChatMessage::getCreateTime, LocalDate.now().withDayOfMonth(1).atStartOfDay())
                        .select(AmChatMessage::getUserId) // 选择 userId 字段
                        .groupBy(AmChatMessage::getUserId)); // 根据 userId 分组
                accountDataVo.setToday_active(activeCount); // 将统计结果设置到 accountDataVo
            }

            return ResultVO.success(amChatBotGreetConfigDataVo);
        } catch (Exception e) {
            log.error("getGreetConfig error id=", e);
        }
        return ResultVO.fail("获取打招呼设置失败");
    }

    @Transactional
    public ResultVO<AmChatbotOptionsConfig> getOptionsConfig(Long userId) {
        try {
            LambdaQueryWrapper<AmChatbotOptionsConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotOptionsConfig::getAdminId, userId);
            AmChatbotOptionsConfig config = amChatbotOptionsConfigService.getOne(queryWrapper);
            // 如果为空,则插入一条默认数据
            if (config == null) {
                config = new AmChatbotOptionsConfig();
                config.setAdminId(userId);
                config.setIsContinueFollow(0);
                amChatbotOptionsConfigService.save(config);
                config = amChatbotOptionsConfigService.getOne(queryWrapper);
            }
            return ResultVO.success(config);
        } catch (Exception e) {
            log.error("getOptionsConfig error userId={}", userId, e);
        }
        return ResultVO.fail("获取方案配置失败");
    }


    @Transactional
    public ResultVO<AmChatbotOptionsConfig> modifyOptionsConfig(UpdateOptionsConfigReq updateOptionsConfigReq, Long adminId) {
        try {
            LambdaQueryWrapper<AmChatbotOptionsConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotOptionsConfig::getAdminId, adminId);
            AmChatbotOptionsConfig config = amChatbotOptionsConfigService.getOne(queryWrapper);
            if (config == null) {
                config = new AmChatbotOptionsConfig();
                config.setAdminId(adminId);
                config.setIsContinueFollow(updateOptionsConfigReq.getIs_continue_follow());
                amChatbotOptionsConfigService.save(config);
                config = amChatbotOptionsConfigService.getOne(queryWrapper);
            } else {
                config.setIsContinueFollow(updateOptionsConfigReq.getIs_continue_follow());
                amChatbotOptionsConfigService.updateById(config);
            }
            return ResultVO.success(config);
        } catch (Exception e) {
            log.error("modifyOptionsConfig error adminId={}", adminId, e);
        }
        return ResultVO.fail("修改方案配置失败");

    }

    @Transactional
    public ResultVO syncPositions(SyncPositionsReq req) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            LambdaQueryWrapper<AmZpLocalAccouts> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmZpLocalAccouts::getId, req.getAccountId());
            queryWrapper.eq(AmZpLocalAccouts::getStatus, 1);
            queryWrapper.ne(AmZpLocalAccouts::getState, AmLocalAccountStatusEnums.OFFLINE.getStatus());
            AmZpLocalAccouts zpLocalAccouts = amZpLocalAccoutsService.getOne(queryWrapper, false);
            if (Objects.isNull(zpLocalAccouts)) {
                return ResultVO.fail("boss账号非在线运行状态，请检查后重试！");
            }
            LambdaQueryWrapper<AmPositionSyncTask> syncTaskQueryWrapper = new LambdaQueryWrapper<>();
            syncTaskQueryWrapper.eq(AmPositionSyncTask::getAccountId, req.getAccountId());
            AmPositionSyncTask amPositionSyncTask = amPositionSyncTaskService.getOne(syncTaskQueryWrapper, false);
            if (Objects.isNull(amPositionSyncTask)) {
                amPositionSyncTask = new AmPositionSyncTask();
                amPositionSyncTask.setAccountId(req.getAccountId());
                amPositionSyncTask.setStatus(PositionSyncTaskStatusEnums.NOT_START.getStatus());
                amPositionSyncTaskService.save(amPositionSyncTask);
            } else {
                if (!(Objects.equals(amPositionSyncTask.getStatus(), PositionSyncTaskStatusEnums.FINISH.getStatus()))) {
                    return ResultVO.success("存在同步中的任务，请勿重复操作");
                } else {
                    amPositionSyncTask.setStatus(PositionSyncTaskStatusEnums.NOT_START.getStatus());
                    amPositionSyncTaskService.updateById(amPositionSyncTask);
                }
            }
            zpLocalAccouts.setIsSync(1);
            boolean updateResult = amZpLocalAccoutsService.updateById(zpLocalAccouts);
            log.info("syncPositions id={},updateResult={}",zpLocalAccouts.getId(), updateResult);
            map.put("boss_id", req.getAccountId());
            map.put("browser_id", zpLocalAccouts.getBrowserId());
            map.put("page", 1);
            AmClientTasks amClientTasks = new AmClientTasks();
            amClientTasks.setId(UUID.randomUUID().toString());
            amClientTasks.setBossId(req.getAccountId());
            amClientTasks.setTaskType(ClientTaskTypeEnums.GET_ALL_JOB.getType());
            amClientTasks.setOrderNumber(ClientTaskTypeEnums.GET_ALL_JOB.getOrder());
            amClientTasks.setStatus(AmClientTaskStatusEnums.NOT_START.getStatus());
            amClientTasks.setData(JSONObject.toJSONString(map));
            amClientTasks.setCreateTime(LocalDateTime.now());
            amClientTasks.setUpdateTime(LocalDateTime.now());
            boolean result = amClientTasksService.save(amClientTasks);
            log.info("syncPositions save amClientTasks result={}", result);
        } catch (Exception e) {
            log.error("modifyOptionsConfig error req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("发送给boss脚本端失败，请联系管理员");
        }
        return ResultVO.success("已发送同步指令，请5分钟后刷新结果");

    }

    @Transactional
    public ResultVO modifyAllStatus(UpdateGreetConfigStatusReq req) {
        try {
            LambdaQueryWrapper<AmChatbotGreetConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotGreetConfig::getAccountId, req.getAccountId());
            AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(queryWrapper);
            if (amChatbotGreetConfig == null) {
                return ResultVO.fail("配置信息不存在");
            }

            if (req.getIsRechatOn() == 1 && (req.getIsAiOn() == 0 && amChatbotGreetConfig.getIsAiOn() == 0)){
                return  ResultVO.fail("AI跟进未开启，请先开启AI跟进") ;
            }

            if (req.getIsAiOn() == 0  &&
                req.getIsRechatOn() == 1 &&
                amChatbotGreetConfig.getIsAiOn() == 1 &&
                amChatbotGreetConfig.getIsRechatOn() == 1 ){
                return  ResultVO.fail("AI复聊未关闭，请先关闭AI复聊") ;
            }

            amChatbotGreetConfig.setIsGreetOn(req.getIsGreetOn());
            amChatbotGreetConfig.setIsAiOn(req.getIsAiOn());
            amChatbotGreetConfig.setIsRechatOn(req.getIsRechatOn());
            amChatbotGreetConfig.setIsAllOn(req.getIsAllOn());
            boolean result = amChatbotGreetConfigService.updateById(amChatbotGreetConfig);
            return result ? ResultVO.success() : ResultVO.fail("修改失败");
        } catch (Exception e) {
            log.error("modifyAllStatus error req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("程序异常,修改全部状态失败");

    }

    @Transactional
    public ResultVO modifyGreetStatus(UpdateGreetConfigStatusReq updateGreetStatusReq) {
        try {
            LambdaQueryWrapper<AmChatbotGreetConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotGreetConfig::getAccountId, updateGreetStatusReq.getAccountId());
            AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(queryWrapper);
            if (amChatbotGreetConfig == null) {
                return ResultVO.fail("配置信息不存在");
            }
            amChatbotGreetConfig.setIsGreetOn(updateGreetStatusReq.getIsGreetOn());
            boolean result = amChatbotGreetConfigService.updateById(amChatbotGreetConfig);
            return result ? ResultVO.success() : ResultVO.fail("修改失败");
        } catch (Exception e) {
            log.error("modifyGreetStatus error req={}", JSONObject.toJSONString(updateGreetStatusReq), e);
        }
        return ResultVO.fail("程序异常,修改修改打招呼任务的状态失败");
    }

    @Transactional
    public ResultVO modifyReChatStatus(UpdateGreetConfigStatusReq updateGreetStatusReq) {
        try {
            LambdaQueryWrapper<AmChatbotGreetConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotGreetConfig::getAccountId, updateGreetStatusReq.getAccountId());
            AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(queryWrapper);
            if (amChatbotGreetConfig == null) {
                return ResultVO.fail("配置信息不存在");
            }
            if (updateGreetStatusReq.getIsRechatOn() == 1 && amChatbotGreetConfig.getIsAiOn() == 0){
                return  ResultVO.fail("AI复聊未关闭，请先关闭AI复聊") ;
            }
            amChatbotGreetConfig.setIsRechatOn(updateGreetStatusReq.getIsRechatOn());
            boolean result = amChatbotGreetConfigService.updateById(amChatbotGreetConfig);
            return result ? ResultVO.success() : ResultVO.fail("修改reChat状态失败");
        } catch (Exception e) {
            log.error("modifyGreetStatus error req={}", JSONObject.toJSONString(updateGreetStatusReq), e);
        }
        return ResultVO.fail("程序异常,修改复聊任务的状态失败");
    }


    @Transactional
    public ResultVO modifyAllOnStatus(UpdateGreetConfigStatusReq updateGreetStatusReq) {
        try {
            LambdaQueryWrapper<AmChatbotGreetConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotGreetConfig::getAccountId, updateGreetStatusReq.getAccountId());
            AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(queryWrapper);
            if (amChatbotGreetConfig == null) {
                return ResultVO.fail("配置信息不存在");
            }
            amChatbotGreetConfig.setIsAllOn(updateGreetStatusReq.getIsAllOn());
            boolean result = amChatbotGreetConfigService.updateById(amChatbotGreetConfig);
            return result ? ResultVO.success() : ResultVO.fail("修改总开关状态失败");
        } catch (Exception e) {
            log.error("modifyGreetStatus error req={}", JSONObject.toJSONString(updateGreetStatusReq), e);
        }
        return ResultVO.fail("程序异常,修改总开关的状态失败");
    }

    @Transactional
    public ResultVO modifyAIOnStatus(UpdateGreetConfigStatusReq updateGreetStatusReq) {
        try {
            LambdaQueryWrapper<AmChatbotGreetConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotGreetConfig::getAccountId, updateGreetStatusReq.getAccountId());
            AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(queryWrapper);
            if (amChatbotGreetConfig == null) {
                return ResultVO.fail("配置信息不存在");
            }
            if (updateGreetStatusReq.getIsAiOn() == 0 && amChatbotGreetConfig.getIsRechatOn() == 1){
                return  ResultVO.fail("复聊任务未关闭,请先关闭复聊任务") ;
            }

            amChatbotGreetConfig.setIsAiOn(updateGreetStatusReq.getIsGreetOn());
            boolean result = amChatbotGreetConfigService.updateById(amChatbotGreetConfig);
            return result ? ResultVO.success() : ResultVO.fail("修改AI跟进状态失败");
        } catch (Exception e) {
            log.error("modifyGreetStatus error req={}", JSONObject.toJSONString(updateGreetStatusReq), e);
        }
        return ResultVO.fail("程序异常,修改AI跟进任务的状态失败");
    }


    @Transactional
    public ResultVO<AmGreetConditionVo> setGreetCondition(AddOrUpdateChatbotGreetConditionNew req) {
        try {
            AmChatbotGreetConditionNew amChatbotGreetConditionNew = AmChatBotGreetNewConditionConvert.I.convertAddOrUpdateGreetNewCondition(req);
            if (Objects.nonNull(req.getId())) {
                // 根据id 更新
                AmChatbotGreetConditionNew chatbotGreetConditionNew = amChatbotGreetConditionNewService.getById(req.getId());
                if (Objects.isNull(chatbotGreetConditionNew)) {
                    boolean updateResult = amChatbotGreetConditionNewService.save(chatbotGreetConditionNew);
                    AmGreetConditionVo amGreetConditionVo = AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(chatbotGreetConditionNew);
                    return updateResult ? ResultVO.success(amGreetConditionVo) : ResultVO.fail("添加打招呼筛选条件失败");
                }
            }else {
                // 根据岗位id 更新
                LambdaQueryWrapper<AmChatbotGreetConditionNew> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AmChatbotGreetConditionNew::getAccountId, req.getAccountId());
                queryWrapper.eq(AmChatbotGreetConditionNew::getPositionId, req.getPositionId());
                AmChatbotGreetConditionNew conditionNewServiceOne = amChatbotGreetConditionNewService.getOne(queryWrapper, false);
                if (Objects.nonNull(conditionNewServiceOne)) {
                    amChatbotGreetConditionNew.setId(conditionNewServiceOne.getId());
                    boolean result = amChatbotGreetConditionNewService.updateById(amChatbotGreetConditionNew);
                    log.info("setGreetCondition update amChatbotGreetCondition result={}", result);
                    AmGreetConditionVo amGreetConditionVo = AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(amChatbotGreetConditionNew);
                    return result ? ResultVO.success(amGreetConditionVo) : ResultVO.fail("修改打招呼筛选条件失败");
                }
            }
            boolean addResult = amChatbotGreetConditionNewService.save(amChatbotGreetConditionNew);
            AmGreetConditionVo amGreetConditionVo = AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(amChatbotGreetConditionNew);
            return addResult ? ResultVO.success(amGreetConditionVo) : ResultVO.fail("修改打招呼筛选条件失败");
        } catch (Exception e) {
            log.error("modifyGreetStatus error req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("程序异常,设置打招呼筛选条件失败");
    }


    /**
     * 先跟着php的逻辑实现..
     */
    @Transactional
    public ResultVO<AmChatbotGreetTask> setGreetTask(AddOrUpdateAmChatbotGreetTask req) {
        try {
            //查询打招呼条件
            if (Objects.nonNull(req.getConditionsId())) {
                AmChatbotGreetConditionNew chatbotGreetConditionNew = amChatbotGreetConditionNewService.getById(req.getConditionsId());
                req.setConditionsId(chatbotGreetConditionNew.getId());
            }else {
                // 根据岗位id 和 账号id
                LambdaQueryWrapper<AmChatbotGreetConditionNew> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AmChatbotGreetConditionNew::getAccountId, req.getAccountId());
                queryWrapper.eq(AmChatbotGreetConditionNew::getPositionId, req.getPositionId());
                AmChatbotGreetConditionNew one = amChatbotGreetConditionNewService.getOne(queryWrapper,false);
                if (Objects.nonNull(one)) {
                    req.setConditionsId(one.getId());
                }
            }
            //有任务数的 有id就更新 没有就新增
            //没有任务数的 有id就删除 没有就不理
            if (Objects.nonNull(req.getTaskNum()) && req.getTaskNum() > 0) {
                if (Objects.nonNull(req.getId())) {
                    AmChatbotGreetTask amChatbotGreetTask = AmChatBotGreetTaskConvert.I.convertAddOrUpdateGreetTask(req);
                    amChatbotGreetTaskService.updateById(amChatbotGreetTask);
                    // 清空缓存
                    String todayDate = RedisKyeConstant.AmChatBotGreetTask +":"+DateUtils.getTodayDate();
                    Long srem = jedisClient.srem(todayDate, amChatbotGreetTask.getId().toString());
                    log.info("setGreetTask srem todayDate={},id={},srem={}",todayDate,amChatbotGreetTask.getId(),srem);
                    return ResultVO.success(amChatbotGreetTask);
                } else {
                    // 检查同个任务时段 task_type 0
                    if (req.getTaskType() == 0) {
                        LambdaQueryWrapper<AmChatbotGreetTask> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(AmChatbotGreetTask::getExecTime, req.getExecTime());
                        queryWrapper.eq(AmChatbotGreetTask::getTaskType, 0);
                        queryWrapper.eq(AmChatbotGreetTask::getAccountId, req.getAccountId());
                        queryWrapper.eq(AmChatbotGreetTask::getPositionId, req.getPositionId());
                        AmChatbotGreetTask amChatbotGreetTask = amChatbotGreetTaskService.getOne(queryWrapper);
                        if (Objects.nonNull(amChatbotGreetTask)) {
                            return ResultVO.fail("每日任务同个时段同个职位，不能新增多个任务，如需修改，请提交对应的任务id");
                        }
                    }
                    req.setCreateTime(LocalDateTime.now());
                    AmChatbotGreetTask amChatbotGreetTask = AmChatBotGreetTaskConvert.I.convertAddOrUpdateGreetTask(req);
                    boolean result = amChatbotGreetTaskService.save(amChatbotGreetTask);
                    log.info("setGreetTask save amChatbotGreetTask amChatbotGreetTask={}, result={}",JSONObject.toJSONString(amChatbotGreetTask), result);
                    req.setId(amChatbotGreetTask.getId());
                    // 处理临时打招呼任务
                    if (req.getTaskType() == 1) {
                        amGreetTaskUtil.dealGreetTask(amChatbotGreetTask);
                    }
                    return ResultVO.success(amChatbotGreetTask);
                }
            } else {
                if (Objects.nonNull(req.getId())) {
                    boolean removeById = amChatbotGreetTaskService.removeById(req.getId());
                    log.info("setGreetTask removeById id={},removeById={}",req.getId(),removeById);
                    return ResultVO.success();
                }
            }
            return ResultVO.success();
        } catch (Exception e) {
            log.error("modifyGreetStatus error req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("程序异常,添加/编辑任务失败");
    }



    /**
     * 先跟着php的逻辑实现..
     */
    @Transactional
    public ResultVO<List<AmChatbotGreetTaskVo>> getGreetTasks(SearchAmChatbotGreetTask req) {
        try {
            // 构建基础查询条件
            LambdaQueryWrapper<AmChatbotGreetTask> baseQuery = buildBaseQuery(req);

            // 按时间段归类
            if (req.getTaskType() == 0) {
                return handleGroupedTasks(req, baseQuery);
            } else {
                return handleUngroupedTasks(baseQuery);
            }
        } catch (Exception e) {
            log.error("getGreetTasks error, req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("程序异常,获取任务列表失败");
        }
    }

    private LambdaQueryWrapper<AmChatbotGreetTask> buildBaseQuery(SearchAmChatbotGreetTask req) {
        LambdaQueryWrapper<AmChatbotGreetTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AmChatbotGreetTask::getAccountId, req.getAccountId())
                .eq(AmChatbotGreetTask::getTaskType, req.getTaskType());
        if (Objects.nonNull(req.getId())) {
            queryWrapper.eq(AmChatbotGreetTask::getId, req.getId());
        }
        if (Objects.nonNull(req.getExecTime())) {
            queryWrapper.eq(AmChatbotGreetTask::getExecTime, req.getExecTime());
        }
        return queryWrapper;
    }

    private ResultVO<List<AmChatbotGreetTaskVo>> handleGroupedTasks(SearchAmChatbotGreetTask req, LambdaQueryWrapper<AmChatbotGreetTask> baseQuery) {
        // 按时间分组并排序
        baseQuery.groupBy(AmChatbotGreetTask::getExecTime)
                .orderByAsc(AmChatbotGreetTask::getId);

        List<AmChatbotGreetTaskVo> groupedTasks = amChatbotGreetTaskService.list(baseQuery).stream()
                .map(AmChatBotGreetTaskConvert.I::convertGreetTaskVo)
                .collect(Collectors.toList());

        for (AmChatbotGreetTaskVo groupedTask : groupedTasks) {
            // 动态更新查询条件
            LambdaQueryWrapper<AmChatbotGreetTask> innerQuery = buildBaseQuery(req);
            if (Objects.isNull(req.getExecTime())) {
                innerQuery.eq(AmChatbotGreetTask::getExecTime, groupedTask.getExecTime());
            }

            // 查询子任务
            List<AmChatbotGreetTaskVo> innerTaskList = amChatbotGreetTaskService.list(innerQuery).stream()
                    .map(AmChatBotGreetTaskConvert.I::convertGreetTaskVo)
                    .peek(this::populateConditionVo) // 填充条件信息
                    .collect(Collectors.toList());

            // 设置子任务和总数
            groupedTask.setTasks(innerTaskList);
            groupedTask.setTotal(innerTaskList.size());
        }

        return ResultVO.success(groupedTasks);
    }

    private ResultVO<List<AmChatbotGreetTaskVo>> handleUngroupedTasks(LambdaQueryWrapper<AmChatbotGreetTask> baseQuery) {
        // 按 ID 排序
        baseQuery.orderByAsc(AmChatbotGreetTask::getId);

        List<AmChatbotGreetTaskVo> tasks = amChatbotGreetTaskService.list(baseQuery).stream()
                .map(AmChatBotGreetTaskConvert.I::convertGreetTaskVo)
                .peek(this::populateConditionVo) // 填充条件信息
                .collect(Collectors.toList());

        return ResultVO.success(tasks);
    }

    private void populateConditionVo(AmChatbotGreetTaskVo taskVo) {
        if (Objects.nonNull(taskVo.getConditionsId())) {
            AmChatbotGreetConditionNew chatbotGreetConditionNew = amChatbotGreetConditionNewService.getById(taskVo.getConditionsId());
            if (chatbotGreetConditionNew != null) {
                AmGreetConditionVo amGreetConditionVo = AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(chatbotGreetConditionNew);
                taskVo.setConditionVo(amGreetConditionVo);
            }
        }
    }


    /**
     * 先跟着php的逻辑实现..
     */
    @Transactional
    public ResultVO deleteGreetTask(Integer id) {
        try {
            boolean result = amChatbotGreetTaskService.removeById(id);
            return result ? ResultVO.success() : ResultVO.fail("删除任务失败");
        } catch (Exception e) {
            log.error("deleteGreetTask error id={}", id, e);
        }
        return ResultVO.fail("程序异常,删除任务失败");
    }

    /**
     * 先跟着php的逻辑实现..
     */
    @Transactional
    public ResultVO<AmGreetConditionVo> getConditionByPositionId(Integer positionId) {
        try {
            LambdaQueryWrapper<AmChatbotGreetConditionNew> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotGreetConditionNew::getPositionId, positionId);
            AmChatbotGreetConditionNew conditionNewServiceOne = amChatbotGreetConditionNewService.getOne(queryWrapper, false);
            return Objects.nonNull(conditionNewServiceOne) ? ResultVO.success(AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(conditionNewServiceOne)) : ResultVO.fail("条件未设置，无数据");
        } catch (Exception e) {
            log.error("getConditionByPositionId error positionId={}", positionId, e);
        }
        return ResultVO.fail("程序异常,删除任务失败");
    }


    /**
     * 先跟着php的逻辑实现..
     */
    @Transactional
    public ResultVO<List<AmChatbotPositionOptionVo>> getPositionOptions(SearchPositionOptions req) {
        try {
            LambdaQueryWrapper<AmChatbotPositionOption> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotPositionOption::getAccountId, req.getAccountId());

            List<AmChatbotPositionOption> amChatbotPositionOptions = amChatbotPositionOptionService.list(queryWrapper);

            if (Objects.nonNull(req.getPositionId())) {
                queryWrapper.eq(AmChatbotPositionOption::getPositionId, req.getPositionId());
                amChatbotPositionOptions = amChatbotPositionOptionService.list(queryWrapper);
            }
            if (CollectionUtils.isEmpty(amChatbotPositionOptions)) {
                return ResultVO.success();
            }
            List<AmChatbotPositionOptionVo> amChatbotPositionOptionVos = amChatbotPositionOptions.stream().map(AmChatBotPositionOptionConvert.I::convertPositionOptionVo).collect(Collectors.toList());
            for (AmChatbotPositionOptionVo amChatbotPositionOption : amChatbotPositionOptionVos) {
                AmMask amMask = amMaskService.getById(amChatbotPositionOption.getAmMaskId());
                if (Objects.nonNull(amMask)){
                    AmMaskVo amMaskVo = AmMaskConvert.I.convertAmMaskVo(amMask);
                    amChatbotPositionOption.setAmMaskVo(amMaskVo);
                }
                amChatbotPositionOption.setAmChatbotOptions(amChatbotOptionsService.getById(amChatbotPositionOption.getRechatOptionId()));
                if (Objects.nonNull(amChatbotPositionOption.getInquiryRechatOptionId())) {
                    amChatbotPositionOption.setInquiryAmChatbotOptions(amChatbotOptionsService.getById(amChatbotPositionOption.getInquiryRechatOptionId()));
                }
                LambdaQueryWrapper<AmChatbotGreetConditionNew> optionQueryWrapper = new LambdaQueryWrapper<>();
                AmChatbotGreetConditionNew conditionNewServiceOne = amChatbotGreetConditionNewService.getOne(optionQueryWrapper.eq(AmChatbotGreetConditionNew::getAccountId, req.getAccountId()).eq(AmChatbotGreetConditionNew::getPositionId, amChatbotPositionOption.getPositionId()), false);
                amChatbotPositionOption.setAmGreetConditionVo(AmChatBotGreetNewConditionConvert.I.convertGreetConditionVo(conditionNewServiceOne));
            }
            return ResultVO.success(amChatbotPositionOptionVos);
        } catch (Exception e) {
            log.error("getPositionOptions error req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("程序异常,删除任务失败");
    }


    /**
     * 先跟着php的逻辑实现..
     */
    @Transactional
    public ResultVO<AmChatbotPositionOption> setPositionOption(AddPositionOptions req) {
        try {
            AmChatbotPositionOption chatbotPositionOption = new AmChatbotPositionOption();
            chatbotPositionOption.setAccountId(req.getAccountId());
            chatbotPositionOption.setPositionId(req.getPositionId());
            chatbotPositionOption.setAmMaskId(req.getAmMaskId());
            chatbotPositionOption.setRechatOptionId(req.getRechatOptionId());
            chatbotPositionOption.setInquiryRechatOptionId(req.getInquiryRechatOptionId());

            LambdaQueryWrapper<AmChatbotPositionOption> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotPositionOption::getAccountId, req.getAccountId());
            queryWrapper.eq(AmChatbotPositionOption::getPositionId, req.getPositionId());
            AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(queryWrapper, false);
            if (Objects.nonNull(amChatbotPositionOption)) {
                amChatbotPositionOption.setAmMaskId(req.getAmMaskId());
                amChatbotPositionOption.setRechatOptionId(req.getRechatOptionId());
                amChatbotPositionOption.setInquiryRechatOptionId(req.getInquiryRechatOptionId());
                amChatbotPositionOptionService.updateById(amChatbotPositionOption);
            } else {
                chatbotPositionOption.setCreateTime((int) (System.currentTimeMillis() / 1000));
                amChatbotPositionOptionService.save(chatbotPositionOption);
            }
            return ResultVO.success(chatbotPositionOption);
        } catch (Exception e) {
            log.error("setPositionOption error req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("程序异常,设置失败");
    }


    /**
     * 获取打招呼条件
     */
    public ResultVO<AmGreetConditionStaticVo> getAmGreetConditionStaticVo(){
        AmGreetConditionStaticVo amGreetConditionStaticVo = new AmGreetConditionStaticVo();
        JSONArray AmGreetEducationVo = new JSONArray();
        for (AmGreetDegreeEnum value : AmGreetDegreeEnum.values()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type",value.getType());
            jsonObject.put("value",value.getValue());
            AmGreetEducationVo.add(jsonObject);
        }
        amGreetConditionStaticVo.setAmGreetEducationVo(AmGreetEducationVo);
        JSONArray AmGreetExperienceVo = new JSONArray();
        for (AmGreetExperienceEnum value : AmGreetExperienceEnum.values()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type",value.getType());
            jsonObject.put("value",value.getValue());
            AmGreetExperienceVo.add(jsonObject);
        }
        amGreetConditionStaticVo.setAmGreetExperienceVo(AmGreetExperienceVo);
        JSONArray AmGreetSalaryVo = new JSONArray();
        for (AmGreetSalaryEnum value : AmGreetSalaryEnum.values()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type",value.getType());
            jsonObject.put("value",value.getValue());
            AmGreetSalaryVo.add(jsonObject);
        }
        amGreetConditionStaticVo.setAmGreetSalaryVo(AmGreetSalaryVo);
        JSONArray AmGreetIntentionVo = new JSONArray();
        for (AmIntentionEnum value : AmIntentionEnum.values()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type",value.getType());
            jsonObject.put("value",value.getValue());
            AmGreetIntentionVo.add(jsonObject);
        }
        amGreetConditionStaticVo.setAmGreetIntentionVo(AmGreetIntentionVo);
        return ResultVO.success(amGreetConditionStaticVo);

    }

}
