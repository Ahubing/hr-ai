package com.open.ai.eros.ai.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.open.ai.eros.ai.bean.req.IcRecordAddReq;
import com.open.ai.eros.ai.bean.req.IcRecordPageReq;
import com.open.ai.eros.ai.bean.req.IcSpareTimeReq;
import com.open.ai.eros.ai.bean.vo.IcGroupDaysVo;
import com.open.ai.eros.ai.bean.vo.IcRecordVo;
import com.open.ai.eros.ai.bean.vo.IcSpareTimeVo;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.common.constants.InterviewRoleEnum;
import com.open.ai.eros.common.constants.InterviewStatusEnum;
import com.open.ai.eros.common.constants.InterviewTypeEnum;
import com.open.ai.eros.ai.convert.IcRecordConvert;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
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
import org.springframework.transaction.annotation.Transactional;

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
public class ICAiManager {

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
    private AmResumeServiceImpl resumeService;

    @Resource
    private AmZpLocalAccoutsServiceImpl accoutsService;

    @Resource
    private AmClientTasksServiceImpl clientTasksService;

    @Resource
    private AmChatMessageServiceImpl chatMessageService;

    @Resource
    private AmZpPlatformsServiceImpl platformsService;

    @Resource
    private JedisClientImpl jedisClient;


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
        String dateStr = CommonConstant.HOLIDAY_PREFIX + date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String value = jedisClient.get(dateStr);
        if(StringUtils.isNotEmpty(value)){
            return "1".equals(value);
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
        if(!req.getStartTime().isAfter(LocalDateTime.now())){
            return ResultVO.fail("预约面试时间有误：" + req.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
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
                    icRecord.setDeptId(section == null ? null : section.getId());
                    icRecord.setDeptName(section == null ? "" : section.getName());
                }
                List<AmResume> resumes = resumeService.list(new LambdaQueryWrapper<AmResume>().eq(AmResume::getUid, req.getEmployeeUid()));
                if(CollectionUtil.isNotEmpty(resumes)){
                    AmResume amResume = resumes.get(0);
                    icRecord.setEmployeeName(amResume.getName());
                    icRecord.setPlatform(amResume.getPlatform());
                    AmZpPlatforms platforms = platformsService.getOne(new LambdaQueryWrapper<AmZpPlatforms>()
                            .eq(AmZpPlatforms::getName, amResume.getPlatform()),false);
                    icRecord.setPlatformId(platforms == null ? null : platforms.getId());
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

    public ResultVO<Boolean> cancelInterview(String icUuid, Integer cancelWho, Boolean checkOnline) {
        return doCancelInterview(icUuid, cancelWho, checkOnline);
    }

    public ResultVO<Boolean> cancelInterview(String icUuid, Integer cancelWho) {
        return doCancelInterview(icUuid, cancelWho, true);
    }

    private ResultVO<Boolean> doCancelInterview(String icUuid, Integer cancelWho, Boolean checkOnline){
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
            if(checkOnline){
                //如果不在线，则报错
                if(!Arrays.asList("free","busy").contains(account.getState())){
                    return ResultVO.fail("请先登录该面试的招聘账号再取消或修改面试");
                }
            }
            //在线则发送消息通知受聘者
            SendMessageUtil.generateAsyncMessage(resume,account,icRecord, "cancel");
            resumeService.updateType(resume, false, ReviewStatusEnums.ABANDON);
            resumeService.updateById(resume);
        }
        boolean update = icRecordService.updateById(icRecord);
        return update ? ResultVO.success(true) : ResultVO.fail("面试取消失败");
    }

    @Transactional
    public ResultVO<Boolean> modifyTime(String icUuid,Integer modifyWho, LocalDateTime newTime) {
        long startTime = System.currentTimeMillis();
        IcRecord icRecord = icRecordService.getById(icUuid);
        long endTime = System.currentTimeMillis();
        log.info("icRecordService.getById:{}" ,endTime - startTime);
        startTime = endTime;
        //招聘者修改面试
        if(InterviewRoleEnum.EMPLOYER.getCode().equals(modifyWho)){
            AmZpLocalAccouts account = accoutsService.getById(icRecord.getAccountId());
            endTime = System.currentTimeMillis();
            log.info("accoutsService.getById:{}" ,endTime - startTime);
            startTime = endTime;
            AmResume resume = resumeService.getOne(new LambdaQueryWrapper<AmResume>()
                    .eq(AmResume::getUid, icRecord.getEmployeeUid()), false);
            endTime = System.currentTimeMillis();
            log.info("resumeService.getOne:{}" ,endTime - startTime);
            startTime = endTime;
            //如果不在线，则报错
            if(!Arrays.asList("free","busy").contains(account.getState())){
                return ResultVO.fail("请先登录该面试的招聘账号再取消或修改面试");
            }
            if(InterviewStatusEnum.CANCEL.getStatus().equals(icRecord.getCancelStatus())){
                return ResultVO.fail("面试已取消，无法修改面试时间");
            }
            //在线则发送消息通知受聘者
            SendMessageUtil.generateAsyncMessage(resume,account,icRecord, "modify");
            endTime = System.currentTimeMillis();
            log.info("generateAsyncMessage:{}" ,endTime - startTime);
            startTime = endTime;
            //取消原来的面试
            icRecord.setCancelTime(LocalDateTime.now());
            icRecord.setCancelStatus(InterviewStatusEnum.CANCEL.getStatus());
            icRecord.setCancelWho(modifyWho);
            icRecordService.updateById(icRecord);
            endTime = System.currentTimeMillis();
            log.info("icRecordService.updateById:{}" ,endTime - startTime);
            startTime = endTime;
            resumeService.updateType(resume, false, ReviewStatusEnums.INVITATION_FOLLOW_UP);
            resumeService.updateById(resume);
            endTime = System.currentTimeMillis();
            log.info("resumeService.updateById:{}" ,endTime - startTime);
            return ResultVO.success(true);
        }

        if(InterviewRoleEnum.EMPLOYEE.getCode().equals(modifyWho)){
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
                boolean update = icRecordService.update(updateWrapper);
                return update ? ResultVO.success(true) : ResultVO.fail("面试时间修改失败");
            }
            return ResultVO.fail("面试时间修改失败，此时间非空闲时间");
        }
        return ResultVO.fail("面试时间修改失败");
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
        Integer deptId = req.getDeptId();
        Integer postId = req.getPostId();
        Integer platformId = req.getPlatformId();
        String employeeUid = req.getEmployeeUid();
        LocalDateTime startTime = req.getStartTime();
        LocalDateTime endTime = req.getEndTime();
        queryWrapper.eq(adminId != null,IcRecord::getAdminId,adminId)
                .like(StringUtils.isNotEmpty(account),IcRecord::getAccount,account)
                .like(StringUtils.isNotEmpty(deptName),IcRecord::getDeptName,deptName)
                .like(StringUtils.isNotEmpty(employeeName),IcRecord::getEmployeeName,employeeName)
                .like(StringUtils.isNotEmpty(postName),IcRecord::getPositionName,postName)
                .like(StringUtils.isNotEmpty(platform),IcRecord::getPlatform,platform)
                .eq(StringUtils.isNotEmpty(type),IcRecord::getInterviewType,type)
                .eq(deptId != null,IcRecord::getDeptId,deptId)
                .eq(postId != null,IcRecord::getPositionId,postId)
                .eq(StringUtils.isNotEmpty(employeeUid),IcRecord::getEmployeeUid,employeeUid)
                .ge(startTime != null,IcRecord::getStartTime,startTime)
                .le(endTime != null,IcRecord::getStartTime,endTime)
                .eq(platformId != null,IcRecord::getPlatformId,platformId)
                .orderByDesc(IcRecord::getStartTime);
        if(status != null){
            if(InterviewStatusEnum.DEPRECATED.getStatus().equals(status)){
                queryWrapper.eq(IcRecord::getCancelStatus,InterviewStatusEnum.NOT_CANCEL.getStatus())
                        .le(IcRecord::getStartTime,LocalDateTime.now());
            }else if(InterviewStatusEnum.NOT_CANCEL.getStatus().equals(status)) {
                queryWrapper.eq(IcRecord::getCancelStatus,status)
                            .gt(IcRecord::getStartTime,LocalDateTime.now());
            }else {
                queryWrapper.eq(IcRecord::getCancelStatus,status);
            }
        }
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
