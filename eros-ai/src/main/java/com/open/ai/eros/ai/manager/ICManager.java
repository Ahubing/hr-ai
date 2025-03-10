package com.open.ai.eros.ai.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.open.ai.eros.ai.bean.req.IcRecordAddReq;
import com.open.ai.eros.ai.bean.req.IcRecordPageReq;
import com.open.ai.eros.ai.bean.req.IcSpareTimeReq;
import com.open.ai.eros.ai.bean.vo.IcGroupDaysVo;
import com.open.ai.eros.ai.bean.vo.IcRecordVo;
import com.open.ai.eros.ai.bean.vo.IcSpareTimeVo;
import com.open.ai.eros.ai.constatns.InterviewRoleEnum;
import com.open.ai.eros.ai.constatns.InterviewStatusEnum;
import com.open.ai.eros.ai.constatns.InterviewTypeEnum;
import com.open.ai.eros.ai.convert.IcRecordConvert;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Date 2025/2/24 20:42
 * @Description 面试日历
 */
@Slf4j
@Component
public class ICManager {

    @Resource
    private IcConfigServiceImpl icConfigService;

    @Resource
    private IcRecordServiceImpl icRecordService;

    @Resource
    private AmNewMaskServiceImpl amNewMaskService;

    @Resource
    private AmPositionSectionServiceImpl sectionService;

    @Resource
    private AmPositionServiceImpl positionService;

    @Resource
    private JedisClientImpl jedisClient;

    @Resource
    private AmResumeServiceImpl resumeService;

    @Resource
    private AmZpLocalAccoutsServiceImpl accoutsService;

    @Resource
    private AmClientTasksServiceImpl clientTasksService;

    @Resource
    private AmChatMessageServiceImpl chatMessageService;

    private static final long EXPIRE_TIME = 5 * 24 * 3600;

    public ResultVO<IcSpareTimeVo> getSpareTime(IcSpareTimeReq spareTimeReq){
        //开始时间参数修正
        if(spareTimeReq.getStartTime().isBefore(LocalDateTime.now())){
            spareTimeReq.setStartTime(LocalDateTime.now());
        }
        AmNewMask mask = amNewMaskService.getById(spareTimeReq.getMaskId());
        log.info("getSpareTime params={} mask={}", JSONObject.toJSONString(spareTimeReq), JSONObject.toJSONString(mask));

        //目前只做群面
        IcSpareTimeVo spareTimeVo = new IcSpareTimeVo();
        List<IcConfig> icConfigs = icConfigService.list(new LambdaQueryWrapper<IcConfig>()
                .eq(IcConfig::getMaskId, spareTimeReq.getMaskId()));

        if(InterviewTypeEnum.GROUP.getCode().equals(mask.getInterviewType())){
            buildGroupSpareTime(spareTimeVo,spareTimeReq,icConfigs,mask.getSkipHolidayStatus());
            return ResultVO.success(spareTimeVo);
        }
        //todo 单面考虑时间冲突处理,并跳过节假日
        buildSingleSpareTime(spareTimeVo);
        return ResultVO.success(spareTimeVo);
    }

    private void buildGroupSpareTime(IcSpareTimeVo spareTimeVo, IcSpareTimeReq spareTimeReq,
                                     List<IcConfig> icConfigs, Integer skipHolidayStatus) {

        List<IcSpareTimeVo.SpareDateVo> spareDateVos = new ArrayList<>();
        LocalDateTime startTime = spareTimeReq.getStartTime();
        LocalDateTime endTime = spareTimeReq.getEndTime();

        // 获取日期范围
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();

        //一周哪些时间段有时间
        if(CollectionUtil.isEmpty(icConfigs)){
            return;
        }

        Map<String, List<IcConfig>> map = icConfigs.stream().collect(Collectors.groupingBy(IcConfig::getDayOfWeek));

        // 遍历每一天
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

            // 节假日跳过
            if (1 == skipHolidayStatus && isHoliday(date)) {
                continue;
            }

            // 获取周几
            int dayOfWeek = date.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
            String key = String.valueOf(dayOfWeek);
            if (map.containsKey(key)) {
                List<IcSpareTimeVo.SparePeriodVo> sparePeriodVos = new ArrayList<>();
                List<IcConfig> icConfigList = map.get(key);
                if (!icConfigList.isEmpty()) {
                    IcConfig icConfig = icConfigList.get(0);
                    LocalDateTime dayStart = date.atStartOfDay();
                    LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
                    LocalDateTime effectiveStart = startTime.isAfter(dayStart) ? startTime : dayStart;
                    LocalDateTime effectiveEnd = endTime.isBefore(dayEnd) ? endTime : dayEnd;

                    if (!effectiveStart.isAfter(effectiveEnd)) {
                        // 处理上午时间段（如果存在）
                        if (icConfig.getMorningStartTime() != null && icConfig.getMorningEndTime() != null) {
                            LocalTime morningStartLocal = icConfig.getMorningStartTime().toLocalTime();
                            LocalTime morningEndLocal = icConfig.getMorningEndTime().toLocalTime();
                            LocalDateTime morningStart = date.atTime(morningStartLocal);
                            LocalDateTime morningEnd = date.atTime(morningEndLocal);
                            LocalDateTime intersectStart = effectiveStart.isAfter(morningStart) ? effectiveStart : morningStart;
                            LocalDateTime intersectEnd = effectiveEnd.isBefore(morningEnd) ? effectiveEnd : morningEnd;
                            if (!intersectStart.isAfter(intersectEnd)) {
                                IcSpareTimeVo.SparePeriodVo sparePeriodVo = new IcSpareTimeVo.SparePeriodVo(intersectStart, intersectEnd);
                                sparePeriodVos.add(sparePeriodVo);
                            }
                        }

                        // 处理下午时间段（如果存在）
                        if (icConfig.getAfternoonStartTime() != null && icConfig.getAfternoonEndTime() != null) {
                            LocalTime afternoonStartLocal = icConfig.getAfternoonStartTime().toLocalTime();
                            LocalTime afternoonEndLocal = icConfig.getAfternoonEndTime().toLocalTime();
                            LocalDateTime afternoonStart = date.atTime(afternoonStartLocal);
                            LocalDateTime afternoonEnd = date.atTime(afternoonEndLocal);
                            LocalDateTime intersectStart = effectiveStart.isAfter(afternoonStart) ? effectiveStart : afternoonStart;
                            LocalDateTime intersectEnd = effectiveEnd.isBefore(afternoonEnd) ? effectiveEnd : afternoonEnd;
                            if (!intersectStart.isAfter(intersectEnd)) {
                                IcSpareTimeVo.SparePeriodVo sparePeriodVo = new IcSpareTimeVo.SparePeriodVo(intersectStart, intersectEnd);
                                sparePeriodVos.add(sparePeriodVo);
                            }
                        }
                    }
                    if(CollectionUtil.isNotEmpty(sparePeriodVos)){
                        spareDateVos.add(new IcSpareTimeVo.SpareDateVo(date, sparePeriodVos));
                    }
                }
            }
        }
        spareTimeVo.setSpareDateVos(spareDateVos);
    }

    private void buildSingleSpareTime(IcSpareTimeVo spareTimeVo) {

    }

    private boolean isHoliday(LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String value = jedisClient.get(dateStr);
        if(StringUtils.isNotEmpty(value)){
            jedisClient.expire(dateStr, EXPIRE_TIME);
            return "1".equals(value);
        }
        try {
            String jsonString = HttpUtil.get(CommonConstant.HOLIDAY_API_URL);
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            JsonObject holidays = json.getAsJsonObject("holidays");
            if(holidays.keySet().contains(dateStr)){
                jedisClient.set(dateStr, "1", EXPIRE_TIME);
                return true;
            }else {
                jedisClient.set(dateStr, "0", EXPIRE_TIME);
            }
        }catch (Exception e){
            log.error("[ic]查询是否是节假日失败:{},错误信息:{}",dateStr,e.getMessage());
        }
        return false;
    }

    public ResultVO<String> appointInterview(IcRecordAddReq req) {
         long count = icRecordService.count(new LambdaQueryWrapper<IcRecord>()
                .eq(IcRecord::getAdminId, req.getAdminId())
                .eq(IcRecord::getPositionId, req.getPositionId())
                .eq(IcRecord::getAccountId, req.getAccountId())
                .eq(IcRecord::getEmployeeUid, req.getEmployeeUid())
                .ge(IcRecord::getStartTime, LocalDateTime.now())
                .eq(IcRecord::getCancelStatus, InterviewStatusEnum.NOT_CANCEL.getStatus()));
        if(count > 0){
            return ResultVO.fail("已经预约了面试,无法再次预约");
        }
        IcRecord icRecord = new IcRecord();
        AmNewMask newMask = amNewMaskService.getById(req.getMaskId());
        if(InterviewTypeEnum.GROUP.getCode().equals(newMask.getInterviewType())){
            IcSpareTimeReq spareTimeReq = new IcSpareTimeReq(req.getMaskId(), req.getStartTime(), req.getStartTime());
            IcSpareTimeVo spareTimeVo = getSpareTime(spareTimeReq).getData();
            if(CollectionUtil.isNotEmpty(spareTimeVo.getSpareDateVos())){
                //如果截止时间十分钟内，则不让预约面试
                if(!notInLimitTenMinutes(req.getStartTime(),req.getMaskId())){
                    return ResultVO.fail("不能在截止时间的十分钟内预约面试");
                }
                BeanUtils.copyProperties(req, icRecord);
                icRecord.setInterviewType(newMask.getInterviewType());
                icRecord.setCancelStatus(InterviewStatusEnum.NOT_CANCEL.getStatus());
                AmPosition position = positionService.getById(req.getPositionId());
                icRecord.setPositionName(position == null ? "" : position.getName());
                if(position != null){
                    AmPositionSection section = sectionService.getById(position.getSectionId());
                    icRecord.setDeptName(section == null ? "" : section.getName());
                }
                List<AmResume> resumes = resumeService.list(new LambdaQueryWrapper<AmResume>().eq(AmResume::getUid, req.getEmployeeUid()));
                if(CollectionUtil.isNotEmpty(resumes)){
                    AmResume amResume = resumes.get(0);
                    icRecord.setEmployeeName(amResume.getName());
                    icRecord.setPlatform(amResume.getPlatform());
                }
                AmZpLocalAccouts account = accoutsService.getById(req.getAccountId());
                if(Objects.nonNull(account)){
                    icRecord.setAccount(account.getAccount());
                }
                icRecordService.save(icRecord);
                return ResultVO.success(icRecord.getId());
            }
            return ResultVO.fail("无空闲时间");
        }
        //todo 单面处理逻辑
        if(icRecordService.save(icRecord)){
            return ResultVO.success(icRecord.getId());
        }
        return ResultVO.fail("预约失败");

    }

    private boolean notInLimitTenMinutes(LocalDateTime startTime, Long maskId) {
        int dayOfWeek = startTime.getDayOfWeek().getValue();
        IcConfig icConfig = icConfigService.getOne(new LambdaQueryWrapper<IcConfig>()
                .eq(IcConfig::getMaskId, maskId)
                .eq(IcConfig::getDayOfWeek, dayOfWeek));
        boolean flag = true;
        Time morningEndTime = icConfig.getMorningEndTime();
        Time morningStartTime = icConfig.getMorningStartTime();
        if(Objects.nonNull(morningEndTime) & Objects.nonNull(morningStartTime)){
            LocalTime meLocalTime = morningEndTime.toLocalTime();
            LocalTime msLocalTime = morningStartTime.toLocalTime();
            LocalTime startLocalTime = startTime.toLocalTime();
            if(!startLocalTime.isBefore(msLocalTime) && !startLocalTime.isAfter(meLocalTime)){
                flag = !startLocalTime.isAfter(meLocalTime.minusMinutes(10));
            }
        }

        Time afternoonEndTime = icConfig.getAfternoonEndTime();
        Time afternoonStartTime = icConfig.getAfternoonStartTime();
        if(Objects.nonNull(afternoonEndTime) & Objects.nonNull(afternoonStartTime)){
            LocalTime aeLocalTime = afternoonEndTime.toLocalTime();
            LocalTime asLocalTime = afternoonStartTime.toLocalTime();
            LocalTime startLocalTime = startTime.toLocalTime();
            if(!startLocalTime.isBefore(asLocalTime) && !startLocalTime.isAfter(aeLocalTime)){
                flag = !startLocalTime.isAfter(aeLocalTime.minusMinutes(10));
            }
        }
        return flag;
    }

    public ResultVO<Boolean> cancelInterview(String icUuid, Integer cancelWho) {
        IcRecord icRecord = icRecordService.getById(icUuid);
        if(InterviewStatusEnum.CANCEL.getStatus().equals(icRecord.getCancelStatus())){
            return ResultVO.fail("已经取消,无法再次取消");
        }
        icRecord.setCancelTime(LocalDateTime.now());
        icRecord.setCancelStatus(InterviewStatusEnum.CANCEL.getStatus());
        icRecord.setCancelWho(cancelWho);
        if(InterviewRoleEnum.EMPLOYER.getCode().equals(cancelWho)){
            AmZpLocalAccouts account = accoutsService.getById(icRecord.getAccountId());
            AmResume resume = resumeService.getOne(new LambdaQueryWrapper<AmResume>()
                    .eq(AmResume::getUid, icRecord.getEmployeeUid()), false);
            //如果不在线，则报错
            if(!Arrays.asList("free","busy").contains(account.getState())){
                return ResultVO.fail("请先登录该面试的招聘账号再取消或修改面试");
            }
            //在线则发送消息通知受聘者
            generateAsyncMessage(resume,account,icRecord, "cancel");

        }
        boolean update = icRecordService.updateById(icRecord);
        return update ? ResultVO.success(true) : ResultVO.fail("面试取消失败");
    }

    public ResultVO<Boolean> modifyTime(String icUuid,Integer modifyWho, LocalDateTime newTime) {
        IcRecord icRecord = icRecordService.getById(icUuid);
        IcSpareTimeReq spareTimeReq = new IcSpareTimeReq(icRecord.getMaskId(), newTime, newTime);
        ResultVO<IcSpareTimeVo> resultVO = getSpareTime(spareTimeReq);
        log.info("modifyTime getSpareTime:{}",resultVO.toString());
        if ((200 != resultVO.getCode())) {
            return ResultVO.fail(501,"面试时间修改失败，无法查询到空闲时间数据");
        }
        IcSpareTimeVo spareTimeVo = resultVO.getData();
        if(CollectionUtil.isNotEmpty(spareTimeVo.getSpareDateVos())){
            LambdaUpdateWrapper<IcRecord> updateWrapper = new LambdaUpdateWrapper<IcRecord>()
                    .eq(IcRecord::getId,icUuid)
                    .set(IcRecord::getModifyTime, LocalDateTime.now())
                    .set(IcRecord::getStartTime, newTime);
            if(InterviewRoleEnum.EMPLOYER.getCode().equals(modifyWho)){
                AmZpLocalAccouts account = accoutsService.getById(icRecord.getAccountId());
                AmResume resume = resumeService.getOne(new LambdaQueryWrapper<AmResume>()
                        .eq(AmResume::getUid, icRecord.getEmployeeUid()), false);
                //如果不在线，则报错
                if(!Arrays.asList("free","busy").contains(account.getState())){
                    return ResultVO.fail("请先登录该面试的招聘账号再取消或修改面试");
                }
                //在线则发送消息通知受聘者
                generateAsyncMessage(resume,account,icRecord, "modify");
            }
            boolean update = icRecordService.update(updateWrapper);
            return update ? ResultVO.success(true) : ResultVO.fail("面试时间修改失败");
        }
        return ResultVO.fail("面试时间修改失败，此时间非空闲时间");
    }

    private void generateAsyncMessage(AmResume resume, AmZpLocalAccouts account, IcRecord record, String type) {
        String content = buildContentByType(record,type);
        AmClientTasks amClientTasks = new AmClientTasks();
        JSONObject jsonObject = new JSONObject();
        JSONObject messageObject = new JSONObject();
        JSONObject searchObject = new JSONObject();
        searchObject.put("encrypt_friend_id", resume.getEncryptGeekId());
        searchObject.put("name", resume.getName());
        messageObject.put("content", content);
        jsonObject.put("user_id", resume.getUid());
        jsonObject.put("message", messageObject);
        jsonObject.put("search_data", searchObject);

        amClientTasks.setTaskType("send_message");
        amClientTasks.setOrderNumber(2);
        amClientTasks.setBossId(resume.getAccountId());
        amClientTasks.setData(jsonObject.toJSONString());
        amClientTasks.setStatus(0);
        amClientTasks.setCreateTime(LocalDateTime.now());
        boolean result = clientTasksService.save(amClientTasks);

        //更新task临时status的状态
        log.info("生成复聊任务处理结果 amClientTask={} result={}", JSONObject.toJSONString(amClientTasks), result);
        if (result) {
            // 生成聊天记录
            AmChatMessage amChatMessage = new AmChatMessage();
            amChatMessage.setConversationId(account.getId() + "_" + resume.getUid());
            amChatMessage.setUserId(Long.parseLong(account.getExtBossId()));
            amChatMessage.setRole(AIRoleEnum.ASSISTANT.getRoleName());
            amChatMessage.setType(-1);
            amChatMessage.setContent(content);
            amChatMessage.setCreateTime(LocalDateTime.now());
            boolean save = chatMessageService.save(amChatMessage);
            log.info("生成聊天记录结果 amChatMessage={} result={}", JSONObject.toJSONString(amChatMessage), save);
        }
    }

    private String buildContentByType(IcRecord record, String type) {
        switch (type){
            case "modify":
                String modifyContent =
                        "感谢您对我们的关注及面试准备。由于我方内部临时出现调整，我们不得不取消原定于[time]的面试安排，对此我们深表歉意。\n" +
                        "若您仍对该岗位感兴趣，我们将在后续招聘计划明确后优先与您联系。\n" +
                        "再次感谢您的理解与支持，祝您求职顺利！";
                return modifyContent.replace("[time]", record.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            case "cancel":
                String cancelContent =
                        "感谢您对我们的关注及面试准备。由于招聘流程调整，我们希望与您协商调整原定于[time]的面试安排。\n" +
                        " 以下为可协调的新时间段，请您确认是否方便：\n" +
                        " [newTime]\n" +
                        " 这里可以查询未来一周内的面试时间\n" +
                        " 若以上时间均不合适，请您提供方便的时间段，我们将尽力配合。\n" +
                        " 如您需进一步沟通，请随时通过与我联系。对此次调整带来的不便，我们深表歉意，并感谢您的理解与配合！";
                StringBuilder newTimeStr = new StringBuilder();
                IcSpareTimeVo spareTimeVo = getSpareTime(new IcSpareTimeReq(record.getMaskId(), LocalDateTime.now(), LocalDateTime.now().plusDays(7))).getData();
                List<IcSpareTimeVo.SpareDateVo> spareDateVos = spareTimeVo.getSpareDateVos();
                for (IcSpareTimeVo.SpareDateVo spareDateVo : spareDateVos) {
                    newTimeStr.append(spareDateVo.getLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("：");
                    newTimeStr.append("   ").append(spareDateVo.getSparePeriodVos().stream().map(sparePeriodVo ->
                            sparePeriodVo.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "至" + sparePeriodVo.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))).collect(Collectors.joining("，"))).append("\n");
                }
                return cancelContent.replace("[newTime]",newTimeStr).replace("[time]", record.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        return "";
    }

    public ResultVO<List<IcGroupDaysVo>> getGroupDaysIC(Long adminId, LocalDate startDate, LocalDate endDate, Integer deptId, Integer postId) {
        List<IcGroupDaysVo> icGroupDaysVos = new ArrayList<>();

        //所有职位
        List<AmPosition> positions = positionService
                .list(new LambdaQueryWrapper<AmPosition>()
                        .eq(deptId != null, AmPosition::getSectionId,deptId)
                        .eq(postId != null, AmPosition::getId,postId)
                        .ne(AmPosition::getIsDeleted,0)
                        .eq(AmPosition::getAdminId, adminId));
        if(CollectionUtil.isEmpty(positions)){
            buildEmptyDays(icGroupDaysVos, startDate, endDate);
            return ResultVO.success(icGroupDaysVos);
        }

        List<AmPositionSection> sectionList = sectionService
                .listByIds(positions.stream().map(AmPosition::getSectionId).collect(Collectors.toSet()));
        if(CollectionUtil.isEmpty(sectionList)){
            buildEmptyDays(icGroupDaysVos, startDate, endDate);
            return ResultVO.success(icGroupDaysVos);
        }

        Map<Long, String> positionMap = new HashMap<>();
        positions.forEach(position -> sectionList.forEach(section -> {
            if(position.getSectionId().equals(section.getId())){
                positionMap.put((long)position.getId(), section.getName());
            }
        }));

        //查询出管理员在时间范围内的所有面试
        List<IcRecord> icRecords = icRecordService.lambdaQuery()
                .eq(IcRecord::getAdminId,adminId)
                .eq(IcRecord::getCancelStatus, InterviewStatusEnum.NOT_CANCEL.getStatus())
                .ge(IcRecord::getStartTime, startDate.atStartOfDay())
                .lt(IcRecord::getStartTime, endDate.plusDays(1).atStartOfDay())
                .list();
        if(CollectionUtil.isEmpty(icRecords)){
            icRecords = new ArrayList<>();
        }

        while (!startDate.isAfter(endDate)){
            LocalDate date = startDate;
            LocalDateTime middleTime = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 12, 0, 0);
            //根据record的startTime按照上午下午分组,查出今天上下午各个部门各有多少场面试
            Map<String, Integer> morningMap = new HashMap<>();
            List<IcRecord> morningRecords = icRecords.stream().filter(record ->
                            record.getStartTime().toLocalDate().equals(date) &&
                            !record.getStartTime().isAfter(middleTime)).collect(Collectors.toList());

            for (IcRecord morningRecord : morningRecords) {
                String positionName = positionMap.get(morningRecord.getPositionId());
                if(StringUtils.isEmpty(positionName)){
                    continue;
                }
                morningMap.put(positionName, morningMap.getOrDefault(positionName, 0) + 1);
            }


            Map<String,Integer> afternoonMap = new HashMap<>();
            List<IcRecord> afternoonRecords = icRecords.stream().filter(record ->
                            record.getStartTime().toLocalDate().equals(date) &&
                            record.getStartTime().isAfter(middleTime)).collect(Collectors.toList());

            for (IcRecord afternoonRecord : afternoonRecords) {
                String positionName = positionMap.get(afternoonRecord.getPositionId());
                if(StringUtils.isEmpty(positionName)){
                    continue;
                }
                afternoonMap.put(positionName, afternoonMap.getOrDefault(positionName, 0) + 1);
            }

            icGroupDaysVos.add(new IcGroupDaysVo(date, morningMap, afternoonMap));
            startDate = startDate.plusDays(1);
        }

        return ResultVO.success(icGroupDaysVos);
    }

    private void buildEmptyDays(List<IcGroupDaysVo> icGroupDaysVos, LocalDate startDate, LocalDate endDate) {
        while (!startDate.isAfter(endDate)){
            LocalDate date = startDate;
            icGroupDaysVos.add(new IcGroupDaysVo(date, new HashMap<>(), new HashMap<>()));
            startDate = startDate.plusDays(1);
        }
    }

    public ResultVO<PageVO<IcRecordVo>> pageIcRecord(IcRecordPageReq req) {
        LambdaQueryWrapper<IcRecord> queryWrapper = new LambdaQueryWrapper<>();
        Long adminId = req.getAdminId();
        Integer status = req.getInterviewStatus();
        String type = req.getInterviewType();
        Integer pageNum = req.getPage();
        Integer pageSize = req.getPageSize();
        String account = req.getAccount();
        String deptName = req.getDeptName();
        String employeeName = req.getEmployeeName();
        String postName = req.getPostName();
        String platform = req.getPlatform();
        queryWrapper.eq(adminId != null,IcRecord::getAdminId,adminId)
                .eq(status != null,IcRecord::getCancelStatus,status)
                .eq(StringUtils.isNotEmpty(account),IcRecord::getAccount,account)
                .eq(StringUtils.isNotEmpty(deptName),IcRecord::getDeptName,deptName)
                .eq(StringUtils.isNotEmpty(employeeName),IcRecord::getEmployeeName,employeeName)
                .eq(StringUtils.isNotEmpty(postName),IcRecord::getPositionName,postName)
                .eq(StringUtils.isNotEmpty(platform),IcRecord::getPlatform,platform)
                .eq(StringUtils.isNotEmpty(type),IcRecord::getInterviewType,type);
        Page<IcRecord> page = new Page<>(pageNum, pageSize);
        Page<IcRecord> icRecordPage = icRecordService.page(page, queryWrapper);
        List<IcRecordVo> icRecordVos = icRecordPage.getRecords().stream().map(IcRecordConvert.I::convertIcRecordVo).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(icRecordVos)){
            icRecordVos.forEach(item->{
                if(InterviewStatusEnum.CANCEL.getStatus().equals(item.getCancelStatus())){
                    return;
                }
                item.setCancelStatus(item.getStartTime().isAfter(LocalDateTime.now()) ? item.getCancelStatus() : InterviewStatusEnum.DEPRECATED.getStatus());
            });
        }
        return ResultVO.success(PageVO.build(icRecordPage.getTotal(), icRecordVos));
    }
}
