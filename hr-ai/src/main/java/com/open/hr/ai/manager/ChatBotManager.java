package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.*;
import com.open.hr.ai.constant.AmClientTaskStatusEnums;
import com.open.hr.ai.constant.PositionSyncTaskStatusEnums;
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
    private AmResumeServiceImpl amResumeService;

    @Resource
    private AmZpPlatformsServiceImpl amZpPlatformsService;

    @Resource
    private AmChatbotOptionsConfigServiceImpl amChatbotOptionsConfigService;

    @Resource
    private AmChatbotGreetConfigServiceImpl amChatbotGreetConfigService;

    @Resource
    private AmChatbotGreetConditionServiceImpl amChatbotGreetConditionService;

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


    public ResultVO<AmZmLocalAccountsListVo> getLocalAccounts(Long adminId) {

        try {
            AmZmLocalAccountsListVo amZmLocalAccountsListVo = new AmZmLocalAccountsListVo();
            LambdaQueryWrapper<AmZpLocalAccouts> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(AmZpLocalAccouts::getAdminId, adminId);

            lambdaQueryWrapper.orderByAsc(AmZpLocalAccouts::getId);
            List<AmZpLocalAccouts> localAccounts = amZpLocalAccoutsService.list(lambdaQueryWrapper);
            if (CollectionUtils.isEmpty(localAccounts)) {
                return ResultVO.success();
            }

            List<AmZpLocalAccoutsVo> amZpLocalAccoutsVos = localAccounts.stream().map(AmZpLocalAccoutsConvert.I::convertAmZpLocalAccounts).collect(Collectors.toList());
            List<AmZpPlatforms> platforms = amZpPlatformsService.list();

            for (AmZpLocalAccoutsVo account : amZpLocalAccoutsVos) {

                if (Objects.isNull(account.getUpdateTime()) || System.currentTimeMillis() / 1000 - account.getUpdateTime().getSecond() > 25) {
                    account.setState("offline");
                }
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

    public ResultVO<AmZpLocalAccouts> AddAccount(AddAccountReq addAccountReq, Long adminId) {
        try {

            LambdaQueryWrapper<AmZpLocalAccouts> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmZpLocalAccouts::getAdminId, adminId);
            queryWrapper.eq(AmZpLocalAccouts::getAccount, addAccountReq.getAccount());
            AmZpLocalAccouts zpLocalAccouts = amZpLocalAccoutsService.getOne(queryWrapper);
            if (Objects.nonNull(zpLocalAccouts)) {
                return ResultVO.fail("账号已存在");
            }
            String uuid = UUID.randomUUID().toString();
            AmZpLocalAccouts amZpLocalAccouts = new AmZpLocalAccouts();
            amZpLocalAccouts.setId(uuid);
            amZpLocalAccouts.setAdminId(adminId);
            amZpLocalAccouts.setAccount(addAccountReq.getAccount());
            amZpLocalAccouts.setMobile(addAccountReq.getMobile());
            amZpLocalAccouts.setCity(addAccountReq.getCity());
            amZpLocalAccouts.setCreateTime(LocalDateTime.now());
            boolean result = amZpLocalAccoutsService.save(amZpLocalAccouts);
            return result ? ResultVO.success("添加成功") : ResultVO.fail("添加失败");
        } catch (Exception e) {
            log.error("AddAccount error", e);
        }
        return ResultVO.fail("添加账号失败");
    }

    public ResultVO deleteAccount(String id) {
        try {
            boolean result = amZpLocalAccoutsService.removeById(id);
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
                    AmChatbotGreetCondition amChatbotGreetCondition = amChatbotGreetConditionService.getOne(new LambdaQueryWrapper<AmChatbotGreetCondition>().eq(AmChatbotGreetCondition::getPositionId, amChatbotGreetTaskVo.getPositionId()), false);
                    if (Objects.nonNull(amChatbotGreetCondition)) {
                        amChatbotGreetTaskVo.setConditionVo(AmChatBotGreetConditionConvert.I.convertGreetConditionVo(amChatbotGreetCondition));
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

            int today_communication = amChatbotGreetResultService.count(new LambdaQueryWrapper<AmChatbotGreetResult>().eq(AmChatbotGreetResult::getAccountId, accountId).ge(AmChatbotGreetResult::getCreateTime, LocalDate.now().atStartOfDay()).eq(AmChatbotGreetResult::getSuccess, 1));
            accountDataVo.setToday_communication(today_communication);

            accountDataVo.setToday_active(0);
            accountDataVo.setToday_rechat(0);

            int t_month_resume = amResumeService.count(new LambdaQueryWrapper<AmResume>().eq(AmResume::getAccountId, accountId).ge(AmResume::getCreateTime, LocalDate.now().withDayOfMonth(1).atStartOfDay()));
            accountDataVo.setT_month_resume(t_month_resume);

            int t_month_communication = amChatbotGreetResultService.count(new LambdaQueryWrapper<AmChatbotGreetResult>().eq(AmChatbotGreetResult::getAccountId, accountId).ge(AmChatbotGreetResult::getCreateTime, LocalDate.now().withDayOfMonth(1).atStartOfDay()).eq(AmChatbotGreetResult::getSuccess, 1));
            accountDataVo.setT_month_communication(t_month_communication);

            accountDataVo.setT_month_active(0);
            accountDataVo.setT_month_rechat(0);

            amChatBotGreetConfigDataVo.setAccountData(accountDataVo);
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
            queryWrapper.ne(AmZpLocalAccouts::getState, "offline");
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
                if (!Objects.equals(amPositionSyncTask.getStatus(), PositionSyncTaskStatusEnums.FINISH.getStatus())) {
                    amPositionSyncTask.setStatus(PositionSyncTaskStatusEnums.NOT_START.getStatus());
                    amPositionSyncTaskService.updateById(amPositionSyncTask);
                } else {
                    return ResultVO.fail("存在同步中的任务，请勿重复操作");
                }
            }

            String taskType = "get_all_job";
            map.put("boss_id", req.getAccountId());
            map.put("browser_id", zpLocalAccouts.getBrowserId());
            map.put("page", 1);
            AmClientTasks amClientTasks = new AmClientTasks();
            amClientTasks.setId(UUID.randomUUID().toString());
            amClientTasks.setBossId(req.getAccountId());
            amClientTasks.setTaskType(taskType);
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
            amChatbotGreetConfig.setIsGreetOn(req.getIsGreetOn());
            amChatbotGreetConfig.setIsAiOn(req.getIsAiOn());
            amChatbotGreetConfig.setIsRechatOn(req.getIsRechatOn());
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
            amChatbotGreetConfig.setIsRechatOn(updateGreetStatusReq.getIsRechatOn());
            boolean result = amChatbotGreetConfigService.updateById(amChatbotGreetConfig);
            return result ? ResultVO.success() : ResultVO.fail("修改reChat状态失败");
        } catch (Exception e) {
            log.error("modifyGreetStatus error req={}", JSONObject.toJSONString(updateGreetStatusReq), e);
        }
        return ResultVO.fail("程序异常,修改复聊任务的状态失败");
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
            amChatbotGreetConfig.setIsAiOn(updateGreetStatusReq.getIsGreetOn());
            boolean result = amChatbotGreetConfigService.updateById(amChatbotGreetConfig);
            return result ? ResultVO.success() : ResultVO.fail("修改AI跟进状态失败");
        } catch (Exception e) {
            log.error("modifyGreetStatus error req={}", JSONObject.toJSONString(updateGreetStatusReq), e);
        }
        return ResultVO.fail("程序异常,修改AI跟进任务的状态失败");
    }


    @Transactional
    public ResultVO<AmChatbotGreetCondition> setGreetCondition(AddOrUpdateChatbotGreetCondition req) {
        try {

            AmChatbotGreetCondition amChatbotGreetCondition = AmChatBotGreetConditionConvert.I.convertAddOrUpdateGreetCondition(req);
            if (Objects.nonNull(req.getId())) {
                AmChatbotGreetCondition amChatbotGreetConfig = amChatbotGreetConditionService.getById(req.getId());
                if (Objects.isNull(amChatbotGreetConfig)) {
                    boolean updateResult = amChatbotGreetConditionService.save(amChatbotGreetCondition);
                    return updateResult ? ResultVO.success(amChatbotGreetConfig) : ResultVO.fail("添加打招呼筛选条件失败");
                }
            }
            boolean addResult = amChatbotGreetConditionService.save(amChatbotGreetCondition);
            return addResult ? ResultVO.success(amChatbotGreetCondition) : ResultVO.fail("修改打招呼筛选条件失败");
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
                LambdaQueryWrapper<AmChatbotGreetTask> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AmChatbotGreetTask::getConditionsId, req.getConditionsId());
                queryWrapper.eq(AmChatbotGreetTask::getPositionId, req.getPositionId());
                queryWrapper.eq(AmChatbotGreetTask::getExecTime, req.getExecTime());
                AmChatbotGreetTask amChatbotGreetTask = amChatbotGreetTaskService.getOne(queryWrapper);
                if (Objects.isNull(amChatbotGreetTask)) {
                    AmChatbotGreetCondition condition = amChatbotGreetConditionService.getById(req.getConditionsId());
                    condition.setId(null);
                    amChatbotGreetConditionService.save(condition);
                    req.setConditionsId(condition.getId());
                }
            }
            if (Objects.nonNull(req.getId())) {
                AmChatbotGreetTask amChatbotGreetTask = AmChatBotGreetTaskConvert.I.convertAddOrUpdateGreetTask(req);
                amChatbotGreetTaskService.updateById(amChatbotGreetTask);
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
                log.info("setGreetTask save amChatbotGreetTask result={}", result);
                req.setId(amChatbotGreetTask.getId());
                // 处理临时打招呼任务
                amGreetTaskUtil.dealGreetTask(amChatbotGreetTask);
                return ResultVO.success(amChatbotGreetTask);
            }
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
            AmChatbotGreetCondition condition = amChatbotGreetConditionService.getById(taskVo.getConditionsId());
            if (condition != null) {
                AmChatbotGreetConditionVo conditionVo = AmChatBotGreetConditionConvert.I.convertGreetConditionVo(condition);
                taskVo.setConditionVo(conditionVo);
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
    public ResultVO<AmChatbotGreetCondition> getConditionByPositionId(Integer positionId) {
        try {
            LambdaQueryWrapper<AmChatbotGreetCondition> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotGreetCondition::getPositionId, positionId);
            AmChatbotGreetCondition amChatbotGreetCondition = amChatbotGreetConditionService.getOne(queryWrapper, false);
            return Objects.nonNull(amChatbotGreetCondition) ? ResultVO.success(amChatbotGreetCondition) : ResultVO.fail("条件未设置，无数据");
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
                LambdaQueryWrapper<AmChatbotGreetCondition> optionQueryWrapper = new LambdaQueryWrapper<>();
                amChatbotPositionOption.setAmChatbotGreetCondition(amChatbotGreetConditionService.getOne(optionQueryWrapper.eq(AmChatbotGreetCondition::getAccountId, req.getAccountId()).eq(AmChatbotGreetCondition::getPositionId, amChatbotPositionOption.getPositionId()), false));
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

            LambdaQueryWrapper<AmChatbotPositionOption> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AmChatbotPositionOption::getAccountId, req.getAccountId());
            queryWrapper.eq(AmChatbotPositionOption::getPositionId, req.getPositionId());
            AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(queryWrapper, false);
            if (Objects.nonNull(amChatbotPositionOption)) {
                amChatbotPositionOption.setAmMaskId(req.getAmMaskId());
                amChatbotPositionOption.setRechatOptionId(req.getRechatOptionId());
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

}
