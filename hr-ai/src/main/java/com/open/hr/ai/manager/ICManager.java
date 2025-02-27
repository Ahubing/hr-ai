package com.open.hr.ai.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.vo.PageVO;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.impl.*;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.hr.ai.bean.req.IcRecordAddReq;
import com.open.hr.ai.bean.req.IcRecordPageReq;
import com.open.hr.ai.bean.req.IcSpareTimeReq;
import com.open.hr.ai.bean.vo.IcGroupDaysVo;
import com.open.hr.ai.bean.vo.IcRecordVo;
import com.open.hr.ai.bean.vo.IcSpareTimeVo;
import com.open.hr.ai.constant.InterviewStatusEnum;
import com.open.hr.ai.constant.InterviewTypeEnum;
import com.open.hr.ai.convert.AmPositionSetionConvertImpl;
import com.open.hr.ai.convert.IcRecordConvert;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    private static final long EXPIRE_TIME = 5 * 24 * 3600;

    public ResultVO<IcSpareTimeVo> getSpareTime(IcSpareTimeReq spareTimeReq){
        //开始时间参数修正
        if(spareTimeReq.getStartTime().isBefore(LocalDateTime.now())){
            spareTimeReq.setStartTime(LocalDateTime.now());
        }
        AmNewMask mask = amNewMaskService.getById(spareTimeReq.getMaskId());
        log.info("getSpareTime mask={}", JSONObject.toJSONString(mask));

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
        IcRecord icRecord = new IcRecord();
        AmNewMask newMask = amNewMaskService.getById(req.getMaskId());
        if(InterviewTypeEnum.GROUP.getCode().equals(newMask.getInterviewType())){
            IcSpareTimeReq spareTimeReq = new IcSpareTimeReq(req.getMaskId(), req.getStartTime(), req.getStartTime());
            IcSpareTimeVo spareTimeVo = getSpareTime(spareTimeReq).getData();
            if(CollectionUtil.isNotEmpty(spareTimeVo.getSpareDateVos())){
                BeanUtils.copyProperties(req, icRecord);
                icRecord.setInterviewType(newMask.getInterviewType());
                icRecord.setCancelStatus(InterviewStatusEnum.NOT_CANCEL.getStatus());
                icRecordService.save(icRecord);
                return ResultVO.success(icRecord.getId());
            }
            return ResultVO.success(null);
        }
        //todo 单面处理逻辑
        icRecordService.save(icRecord);
        return ResultVO.success(icRecord.getId());
    }

    public ResultVO<Boolean> cancelInterview(String icUuid, Integer cancelWho) {
        LambdaUpdateWrapper<IcRecord> updateWrapper = new LambdaUpdateWrapper<IcRecord>()
                .eq(IcRecord::getId,icUuid)
                .eq(IcRecord::getCancelStatus,InterviewStatusEnum.NOT_CANCEL.getStatus())
                .set(IcRecord::getCancelTime, LocalDateTime.now())
                .set(IcRecord::getCancelStatus, InterviewStatusEnum.CANCEL.getStatus())
                .set(IcRecord::getCancelWho, cancelWho);
        boolean update = icRecordService.update(updateWrapper);
        return update ? ResultVO.success(true) : ResultVO.fail("面试取消失败");
    }

    public ResultVO<Boolean> modifyTime(String icUuid, LocalDateTime newTime) {
        IcRecord icRecord = icRecordService.getById(icUuid);
        IcSpareTimeReq spareTimeReq = new IcSpareTimeReq(icRecord.getMaskId(), newTime, newTime);
        ResultVO<IcSpareTimeVo> resultVO = getSpareTime(spareTimeReq);
        if ((200 != resultVO.getCode())) {
            return ResultVO.fail("面试时间修改失败，无法查询到空闲时间数据");
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
            return ResultVO.success(icGroupDaysVos);
        }

        List<AmPositionSection> sectionList = sectionService
                .listByIds(positions.stream().map(AmPosition::getSectionId).collect(Collectors.toSet()));
        if(CollectionUtil.isEmpty(sectionList)){
            return ResultVO.success(icGroupDaysVos);
        }

        Map<Integer, String> positionMap = new HashMap<>();
        positions.forEach(position -> sectionList.forEach(section -> {
            if(position.getSectionId().equals(section.getId())){
                positionMap.put(position.getId(), section.getName());
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
            //根据record的startTime按照上午下午分组,查出今天上下午各个职位各有多少场面试
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

    public ResultVO<PageVO<IcRecordVo>> pageIcRecord(IcRecordPageReq req) {
        LambdaQueryWrapper<IcRecord> queryWrapper = new LambdaQueryWrapper<>();
        Long adminId = req.getAdminId();
        Integer status = req.getInterviewStatus();
        String type = req.getInterviewType();
        Integer pageNum = req.getPage();
        Integer pageSize = req.getPageSize();
        queryWrapper.eq(adminId != null,IcRecord::getAdminId,adminId)
                    .eq(status != null,IcRecord::getCancelStatus,status)
                    .eq(StringUtils.isNotEmpty(type),IcRecord::getInterviewType,type);
        Page<IcRecord> page = new Page<>(pageNum, pageSize);
        Page<IcRecord> icRecordPage = icRecordService.page(page, queryWrapper);
        List<IcRecordVo> icRecordVos = icRecordPage.getRecords().stream().map(IcRecordConvert.I::convertIcRecordVo).collect(Collectors.toList());

        return ResultVO.success(PageVO.build(icRecordPage.getTotal(), icRecordVos));
    }
}
