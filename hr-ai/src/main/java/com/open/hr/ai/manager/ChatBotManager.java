package com.open.hr.ai.manager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.hr.ai.bean.req.*;
import com.open.hr.ai.bean.vo.AmChatbotGreetConditionVo;
import com.open.hr.ai.bean.vo.AmChatbotGreetTaskVo;
import com.open.hr.ai.bean.vo.AmChatbotPositionOptionVo;
import com.open.hr.ai.bean.vo.AmZpLocalAccoutsVo;
import com.open.hr.ai.convert.AmChatBotGreetConditionConvert;
import com.open.hr.ai.convert.AmChatBotGreetTaskConvert;
import com.open.hr.ai.convert.AmChatBotPositionOptionConvert;
import com.open.hr.ai.convert.AmZpLocalAccoutsConvert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Date 2025/1/4 13:32
 */
@Component
@Slf4j
public class ChatBotManager {

    @Resource
    private AmZpLocalAccoutsServiceImpl amZpLocalAccoutsService;

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
    private AmSquareRolesServiceImpl amSquareRolesService;

    @Resource
    private AmChatbotOptionsServiceImpl amChatbotOptionsService;



    @Resource
    private AmPositionSyncTaskServiceImpl amPositionSyncTaskService;

    @Resource
    private AmClientTasksServiceImpl amClientTasksService;

    @Resource
    private AmZpLocalAccoutsConvert amZpLocalAccoutsConvert;


    public ResultVO getLocalAccounts(Long adminId) {

        try {
            LambdaQueryWrapper<AmZpLocalAccouts> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(AmZpLocalAccouts::getAdminId, adminId);

            lambdaQueryWrapper.orderByAsc(AmZpLocalAccouts::getId);
            List<AmZpLocalAccouts> localAccounts = amZpLocalAccoutsService.list(lambdaQueryWrapper);
            if (CollectionUtils.isEmpty(localAccounts)) {
                return ResultVO.success();
            }

            List<AmZpLocalAccoutsVo> amZpLocalAccoutsVos = localAccounts.stream().map(amZpLocalAccoutsConvert::convertAmZpLocalAccounts).collect(Collectors.toList());
            List<AmZpPlatforms> platforms = amZpPlatformsService.list();

            for (AmZpLocalAccoutsVo account : amZpLocalAccoutsVos) {
                if (System.currentTimeMillis() / 1000 - account.getUpdateTime().getSecond() > 25) {
                    account.setState("inactive");
                }
                for (AmZpPlatforms platform : platforms) {
                    if (platform.getId().equals(account.getPlatformId())) {
                        account.setPlatform(platform.getName());
                    }
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", localAccounts);
            data.put("platforms", platforms);
            data.put("citys", Arrays.asList("北京", "上海", "深圳", "广州", "杭州", "成都"));
            return ResultVO.success(data);
        } catch (Exception e) {
            log.error("getLocalAccounts error", e);
        }
        return ResultVO.fail("获取账号列表失败");
    }


    public ResultVO getPlatforms() {
        try {
            List<AmZpPlatforms> platforms = amZpPlatformsService.list();
            return ResultVO.success(platforms);
        } catch (Exception e) {
            log.error("getPlatforms error", e);
        }
        return ResultVO.fail("获取平台列表失败");
    }

    public ResultVO AddAccount(AddAccountReq addAccountReq) {
        try {

            QueryWrapper<AmZpLocalAccouts> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admin_id", addAccountReq.getAdminId());
            queryWrapper.eq("account", addAccountReq.getAccount());
            AmZpLocalAccouts zpLocalAccouts = amZpLocalAccoutsService.getOne(queryWrapper);
            if (Objects.nonNull(zpLocalAccouts)) {
                return ResultVO.fail("账号已存在");
            }
            String uuid = UUID.randomUUID().toString();
            AmZpLocalAccouts amZpLocalAccouts = new AmZpLocalAccouts();
            amZpLocalAccouts.setId(uuid);
            amZpLocalAccouts.setAdminId(addAccountReq.getAdminId());
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

    public ResultVO getGreetByAccountId(String id) {
        try {
            AmZpLocalAccouts amZpLocalAccouts = amZpLocalAccoutsService.getById(id);
            if (Objects.isNull(amZpLocalAccouts)) {
                return ResultVO.fail("账号不存在");
            }
            AmZpLocalAccoutsVo amZpLocalAccoutsVo = amZpLocalAccoutsConvert.convertAmZpLocalAccounts(amZpLocalAccouts);
            return ResultVO.success(amZpLocalAccoutsVo);
        } catch (Exception e) {
            log.error("getGreetByAccountId error id=", e);
        }
        return ResultVO.fail("获取打招呼设置失败");
    }

    @Transactional
    public ResultVO getOptionsConfig(Long userId) {
        try {
            QueryWrapper<AmChatbotOptionsConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admin_id", userId);
            AmChatbotOptionsConfig config = amChatbotOptionsConfigService.getOne(queryWrapper);
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
    public ResultVO modifyOptionsConfig(UpdateOptionsConfigReq updateOptionsConfigReq) {
        try {
            QueryWrapper<AmChatbotOptionsConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admin_id", updateOptionsConfigReq.getAdminId());
            AmChatbotOptionsConfig config = amChatbotOptionsConfigService.getOne(queryWrapper);
            if (config == null) {
                config = new AmChatbotOptionsConfig();
                config.setAdminId(updateOptionsConfigReq.getAdminId());
                config.setIsContinueFollow(updateOptionsConfigReq.getIs_continue_follow());
                amChatbotOptionsConfigService.save(config);
                config = amChatbotOptionsConfigService.getOne(queryWrapper);
            } else {
                config.setIsContinueFollow(updateOptionsConfigReq.getIs_continue_follow());
                amChatbotOptionsConfigService.updateById(config);
            }
            return ResultVO.success(config);
        } catch (Exception e) {
            log.error("modifyOptionsConfig error userId={}", updateOptionsConfigReq.getAdminId(), e);
        }
        return ResultVO.fail("修改方案配置失败");

    }

    @Transactional
    public ResultVO syncPositions(SyncPositionsReq req) {
        HashMap<String,Object> map = new HashMap<>();
        try {
            String taskType = "get_all_job";
            QueryWrapper<AmZpLocalAccouts> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", req.getAccountId());
            queryWrapper.eq("state", "active");
            AmZpLocalAccouts zpLocalAccouts = amZpLocalAccoutsService.getOne(queryWrapper, false);
            if (Objects.isNull(zpLocalAccouts)){
                return ResultVO.fail("boss账号非在线运行状态，请检查后重试！");
            }
            map.put("boss_id",req.getAccountId());
            map.put("browser_id",zpLocalAccouts.getBrowserId());
            map.put("page",1);
            QueryWrapper<AmPositionSyncTask> syncTaskQueryWrapper = new QueryWrapper<>();
            syncTaskQueryWrapper.eq("account_id",req.getAccountId());
            AmPositionSyncTask amPositionSyncTask = amPositionSyncTaskService.getOne(syncTaskQueryWrapper, false);
            if (Objects.isNull(amPositionSyncTask)){
                amPositionSyncTask = new AmPositionSyncTask();
                amPositionSyncTask.setAccountId(req.getAccountId());
                amPositionSyncTask.setStatus(0);
                amPositionSyncTaskService.save(amPositionSyncTask);

                AmClientTasks amClientTasks = new AmClientTasks();
                amClientTasks.setId(UUID.randomUUID().toString());
                amClientTasks.setBossId(req.getAccountId());
                amClientTasks.setTaskType(taskType);
                amClientTasks.setStatus(0);
                amClientTasks.setData(JSONObject.toJSONString(map));
                amClientTasks.setCreateTime(LocalDateTime.now());
                amClientTasks.setUpdateTime(LocalDateTime.now());
                amClientTasksService.save(amClientTasks);
                //TODO:  php  sendToWap($account_id,'job_list',$_data); 是什么实现
            }else {
                if(amPositionSyncTask.getStatus() ==2){
                    amPositionSyncTask.setStatus(0);
                    amPositionSyncTaskService.updateById(amPositionSyncTask);

                    AmClientTasks amClientTasks = new AmClientTasks();
                    amClientTasks.setId(UUID.randomUUID().toString());
                    amClientTasks.setBossId(req.getAccountId());
                    amClientTasks.setTaskType(taskType);
                    amClientTasks.setStatus(0);
                    amClientTasks.setData(JSONObject.toJSONString(map));
                    amClientTasks.setCreateTime(LocalDateTime.now());
                    amClientTasks.setUpdateTime(LocalDateTime.now());
                    amClientTasksService.save(amClientTasks);
                    //TODO:  php  sendToWap($account_id,'job_list',$_data);  是什么实现
                }else {
                    return ResultVO.fail(JSONObject.toJSONString(map));
                }
            }
        } catch (Exception e) {
            log.error("modifyOptionsConfig error req={}", JSONObject.toJSONString(req), e);
            return ResultVO.fail("发送给boss脚本端失败，请联系管理员");
        }
        return ResultVO.success("已发送同步指令，请5分钟后刷新结果");

    }

    @Transactional
    public ResultVO modifyAllStatus(UpdateGreetConfigStatusReq req) {
        try {
            QueryWrapper<AmChatbotGreetConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", req.getAccountId());
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
            QueryWrapper<AmChatbotGreetConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", updateGreetStatusReq.getAccountId());
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
            QueryWrapper<AmChatbotGreetConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", updateGreetStatusReq.getAccountId());
            AmChatbotGreetConfig amChatbotGreetConfig = amChatbotGreetConfigService.getOne(queryWrapper);
            if (amChatbotGreetConfig == null) {
                return ResultVO.fail("配置信息不存在");
            }
            amChatbotGreetConfig.setIsRechatOn(updateGreetStatusReq.getIsGreetOn());
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
            QueryWrapper<AmChatbotGreetConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", updateGreetStatusReq.getAccountId());
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
    public ResultVO setGreetCondition(AddOrUpdateChatbotGreetCondition req) {
        try {

            AmChatbotGreetCondition amChatbotGreetCondition = AmChatBotGreetConditionConvert.I.convertAddOrUpdateGreetCondition(req);
            if (Objects.nonNull(req.getId())) {
                AmChatbotGreetCondition amChatbotGreetConfig = amChatbotGreetConditionService.getById(req.getId());
                if (Objects.isNull(amChatbotGreetConfig)) {
                    boolean updateResult = amChatbotGreetConditionService.save(amChatbotGreetCondition);
                    return updateResult ? ResultVO.success() : ResultVO.fail("添加打招呼筛选条件失败");
                }
            }
            boolean addResult = amChatbotGreetConditionService.save(amChatbotGreetCondition);
            return addResult ? ResultVO.success() : ResultVO.fail("修改打招呼筛选条件失败");
        } catch (Exception e) {
            log.error("modifyGreetStatus error req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("程序异常,设置打招呼筛选条件失败");
    }


     /**
     * 先跟着php的逻辑实现..
     */
    @Transactional
    public ResultVO setGreetTask(AddOrUpdateAmChatbotGreetTask req) {
        try {
            if (Objects.nonNull(req.getConditionsId())) {
                QueryWrapper<AmChatbotGreetTask> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("conditions_id", req.getConditionsId());
                queryWrapper.eq("position_id", req.getPositionId());
                queryWrapper.eq("exec_time", req.getExecTime());
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
            } else {
                // 检查同个任务时段 task_type 0
                if (req.getTaskType() == 0) {
                    QueryWrapper<AmChatbotGreetTask> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("exec_time", req.getExecTime());
                    queryWrapper.eq("task_type", 0);
                    queryWrapper.eq("account_id", req.getAccountId());
                    queryWrapper.eq("position_id", req.getPositionId());
                    AmChatbotGreetTask amChatbotGreetTask = amChatbotGreetTaskService.getOne(queryWrapper);
                    if (Objects.nonNull(amChatbotGreetTask)) {
                        return ResultVO.fail("每日任务同个时段同个职位，不能新增多个任务，如需修改，请提交对应的任务id");
                    }
                }
                // 跟着php 以秒进行存储..
                req.setCreateTime((int) (System.currentTimeMillis() / 1000));
                AmChatbotGreetTask amChatbotGreetTask = AmChatBotGreetTaskConvert.I.convertAddOrUpdateGreetTask(req);
                amChatbotGreetTaskService.save(amChatbotGreetTask);
                req.setId(amChatbotGreetTask.getId());
            }
            return ResultVO.success(req);
        } catch (Exception e) {
            log.error("modifyGreetStatus error req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("程序异常,添加/编辑任务失败");
    }


     /**
     * 先跟着php的逻辑实现..
     */
    @Transactional
    public ResultVO getGreetTasks(SearchAmChatbotGreetTask req) {
        QueryWrapper<AmChatbotGreetTask> queryWrapper = new QueryWrapper<>();
        QueryWrapper<AmChatbotGreetTask> queryWrapperInner = new QueryWrapper<>();

        queryWrapper.eq("account_id", req.getAccountId());
        queryWrapper.eq("task_type", req.getTaskType());
        queryWrapperInner.eq("account_id", req.getAccountId());
        queryWrapperInner.eq("task_type", req.getTaskType());
        if (Objects.nonNull(req.getId())) {
            queryWrapper.eq("id", req.getId());
            queryWrapperInner.eq("id", req.getId());
        }
        if (Objects.nonNull(req.getExecTime())) {
            queryWrapper.eq("exec_time", req.getExecTime());
            queryWrapperInner.eq("exec_time", req.getExecTime());
        }
        try {
            //按时间段归类
            if (req.getTaskType() == 0) {
                queryWrapper.groupBy("exec_time");
                queryWrapper.orderByAsc("id");
                List<AmChatbotGreetTaskVo> amChatbotGreetTaskVoList = amChatbotGreetTaskService.list(queryWrapper).stream().map(AmChatBotGreetTaskConvert.I::convertGreetTaskVo).collect(Collectors.toList());
                for (AmChatbotGreetTaskVo amChatbotGreetTask : amChatbotGreetTaskVoList) {
                    if (Objects.isNull(req.getExecTime())) {
                        queryWrapperInner.eq("exec_time", amChatbotGreetTask.getExecTime());
                    }
                    List<AmChatbotGreetTaskVo> innerTaskList = amChatbotGreetTaskService.list(queryWrapperInner).stream().map(AmChatBotGreetTaskConvert.I::convertGreetTaskVo).collect(Collectors.toList());
                    for (AmChatbotGreetTaskVo chatbotGreetTask : innerTaskList) {
                        if (Objects.nonNull(chatbotGreetTask.getConditionsId())) {
                            AmChatbotGreetCondition condition = amChatbotGreetConditionService.getById(chatbotGreetTask.getConditionsId());
                            AmChatbotGreetConditionVo amChatbotGreetConditionVo = AmChatBotGreetConditionConvert.I.convertGreetConditionVo(condition);
                            chatbotGreetTask.setConditionVo(amChatbotGreetConditionVo);
                        }
                    }
                    amChatbotGreetTask.setTasks(innerTaskList);
                    amChatbotGreetTask.setTotal(innerTaskList.size());
                }
                return ResultVO.success(amChatbotGreetTaskVoList);
            } 
            else {
                queryWrapper.orderByAsc("id");
                List<AmChatbotGreetTaskVo> amChatbotGreetTaskVos = amChatbotGreetTaskService.list(queryWrapper).stream().map(AmChatBotGreetTaskConvert.I::convertGreetTaskVo).collect(Collectors.toList());
                    for (AmChatbotGreetTaskVo chatbotGreetTask : amChatbotGreetTaskVos) {
                        if (Objects.nonNull(chatbotGreetTask.getConditionsId())) {
                            AmChatbotGreetCondition condition = amChatbotGreetConditionService.getById(chatbotGreetTask.getConditionsId());
                            AmChatbotGreetConditionVo amChatbotGreetConditionVo = AmChatBotGreetConditionConvert.I.convertGreetConditionVo(condition);
                            chatbotGreetTask.setConditionVo(amChatbotGreetConditionVo);
                        }
                    }
                return ResultVO.success(amChatbotGreetTaskVos);
            }
        } catch (Exception e) {
            log.error("getGreetTask error req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("程序异常,获取任务列表失败");
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
    public ResultVO getConditionByPositionId(Integer positionId) {
        try {
            QueryWrapper<AmChatbotGreetCondition> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("position_id", positionId);
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
    public ResultVO getPositionOptions(SearchPositionOptions req) {
        try {
            QueryWrapper<AmChatbotPositionOption> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", req.getAccountId());

            List<AmChatbotPositionOption> amChatbotPositionOptions = amChatbotPositionOptionService.list(queryWrapper);

            if (Objects.nonNull(req.getPositionId())){
                queryWrapper.eq("position_id", req.getPositionId());
                amChatbotPositionOptions =  amChatbotPositionOptionService.list(queryWrapper);
            }
            if (CollectionUtils.isEmpty(amChatbotPositionOptions)) {
                return ResultVO.success();
            }
            List<AmChatbotPositionOptionVo> amChatbotPositionOptionVos = amChatbotPositionOptions.stream().map(AmChatBotPositionOptionConvert.I::convertPositionOptionVo).collect(Collectors.toList());
            for (AmChatbotPositionOptionVo amChatbotPositionOption : amChatbotPositionOptionVos) {
                amChatbotPositionOption.setAmSquareRoles(amSquareRolesService.getById(amChatbotPositionOption.getSquareRoleId()));
                amChatbotPositionOption.setAmChatbotOptions( amChatbotOptionsService.getById(amChatbotPositionOption.getRechatOptionId()));
                QueryWrapper<AmChatbotGreetCondition> optionQueryWrapper = new QueryWrapper<>();
                amChatbotPositionOption.setAmChatbotGreetCondition(amChatbotGreetConditionService.getOne(optionQueryWrapper.eq("account_id",req.getAccountId()).eq("position_id",amChatbotPositionOption.getPositionId()), false));
            }
            return  ResultVO.success(amChatbotPositionOptionVos);
        } catch (Exception e) {
            log.error("getPositionOptions error req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("程序异常,删除任务失败");
    }



    /**
     * 先跟着php的逻辑实现..
     */
    @Transactional
    public ResultVO setPositionOption(AddPositionOptions req) {
        try {

            AmChatbotPositionOption chatbotPositionOption = new AmChatbotPositionOption();
            chatbotPositionOption.setAccountId(req.getAccountId());
            chatbotPositionOption.setPositionId(req.getPositionId());
            chatbotPositionOption.setSquareRoleId(req.getSquareRoleId());
            chatbotPositionOption.setRechatOptionId(req.getRechatOptionId());

            QueryWrapper<AmChatbotPositionOption> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id", req.getAccountId());
            queryWrapper.eq("position_id", req.getPositionId());
            AmChatbotPositionOption amChatbotPositionOption = amChatbotPositionOptionService.getOne(queryWrapper,false);
            if (Objects.nonNull(amChatbotPositionOption)) {
                amChatbotPositionOption.setSquareRoleId(req.getSquareRoleId());
                amChatbotPositionOption.setRechatOptionId(req.getRechatOptionId());
                amChatbotPositionOptionService.updateById(amChatbotPositionOption);
            } else {
                chatbotPositionOption.setCreateTime((int) (System.currentTimeMillis() / 1000));
                amChatbotPositionOptionService.save(chatbotPositionOption);
            }
            return  ResultVO.success(chatbotPositionOption);
        } catch (Exception e) {
            log.error("setPositionOption error req={}", JSONObject.toJSONString(req), e);
        }
        return ResultVO.fail("程序异常,设置失败");
    }

}
