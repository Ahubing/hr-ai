package com.open.hr.ai.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.db.mysql.hr.entity.AmNewMask;
import com.open.ai.eros.db.mysql.hr.entity.IcConfig;
import com.open.ai.eros.db.mysql.hr.service.impl.AmNewMaskServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.IcConfigServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.IcRecordServiceImpl;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.hr.ai.bean.req.IcSpareTimeReq;
import com.open.hr.ai.bean.vo.IcSpareTimeVo;
import com.open.hr.ai.constant.InterviewTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private JedisClientImpl jedisClient;

    private static final long EXPIRE_TIME = 5 * 24 * 3600;

    public IcSpareTimeVo getSpareTime(IcSpareTimeReq spareTimeReq){
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
            return spareTimeVo;
        }
        //todo 单面考虑时间冲突处理,并跳过节假日
        buildSingleSpareTime(spareTimeVo);
        return spareTimeVo;
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

                    if (effectiveStart.isBefore(effectiveEnd)) {
                        // 处理上午时间段（如果存在）
                        if (icConfig.getMorningStartTime() != null && icConfig.getMorningEndTime() != null) {
                            LocalTime morningStartLocal = icConfig.getMorningStartTime().toLocalTime();
                            LocalTime morningEndLocal = icConfig.getMorningEndTime().toLocalTime();
                            LocalDateTime morningStart = date.atTime(morningStartLocal);
                            LocalDateTime morningEnd = date.atTime(morningEndLocal);
                            LocalDateTime intersectStart = effectiveStart.isAfter(morningStart) ? effectiveStart : morningStart;
                            LocalDateTime intersectEnd = effectiveEnd.isBefore(morningEnd) ? effectiveEnd : morningEnd;
                            if (intersectStart.isBefore(intersectEnd)) {
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
                            if (intersectStart.isBefore(intersectEnd)) {
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

}
