package com.open.hr.ai.manager;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.open.ai.eros.db.mysql.hr.entity.IcConfig;
import com.open.ai.eros.db.mysql.hr.service.impl.IcConfigServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.IcRecordServiceImpl;
import com.open.ai.eros.db.redis.RedisFactory;
import com.open.ai.eros.db.redis.impl.JedisClientImpl;
import com.open.ai.eros.db.util.RedisPoolUtils;
import com.open.hr.ai.bean.req.IcSpareTimeReq;
import com.open.hr.ai.bean.vo.IcSpareTimeVo;
import com.open.hr.ai.constant.InterviewTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    private JedisClientImpl jedisClient;

    private static final String HOLIDAY_API_URL = "http://timor.tech/api/holiday/info/";

    private static final long EXPIRE_TIME = 5 * 24 * 3600;

    public IcSpareTimeVo getSpareTime(IcSpareTimeReq spareTimeReq){
        //目前只做群面，简单返回
        IcSpareTimeVo spareTimeVo = new IcSpareTimeVo();
        IcConfig icConfig = icConfigService.getOne(new LambdaQueryWrapper<IcConfig>()
                .eq(IcConfig::getMaskId, spareTimeReq.getMaskId()));
        spareTimeVo.setInterviewType(icConfig.getType());
        if(InterviewTypeEnum.GROUP.getCode().equals(icConfig.getType())){
            buildGroupSpareTime(spareTimeVo,spareTimeReq,icConfig);
            return spareTimeVo;
        }
        //todo 单面考虑时间冲突处理,并跳过节假日
        singleSkipHoliday(spareTimeVo);
        return spareTimeVo;
    }

    private void buildGroupSpareTime(IcSpareTimeVo spareTimeVo, IcSpareTimeReq spareTimeReq, IcConfig icConfig) {

        List<IcSpareTimeVo.SpareDateVo> spareDateVos = new ArrayList<>();
        LocalDateTime startTime = spareTimeReq.getStartTime();
        LocalDateTime endTime = spareTimeReq.getEndTime();

        // 获取开始和结束日期
        LocalDate startDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();

        // 遍历每一天
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

            // 节假日跳过
            if (1 == icConfig.getSkipHolidayStatus() && isHoliday(date)) {
                continue;
            }

            // 定义当天的关键时间点
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN); // 00:00
            LocalDateTime dayNoon = LocalDateTime.of(date, LocalTime.of(12, 0)); // 12:00
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX); // 23:59:59

            // 计算有效开始和结束时间
            LocalDateTime effectiveStart = startTime.isAfter(dayStart) ? startTime : dayStart;
            LocalDateTime effectiveEnd = endTime.isBefore(dayEnd) ? endTime : dayEnd;

            // 判断时间段类型
            int type;
            if (!effectiveStart.isAfter(dayNoon) && effectiveEnd.isAfter(dayNoon)) {
                type = 3; // 全天：开始时间 <= 12:00 且结束时间 > 12:00
            } else if (effectiveStart.isAfter(dayNoon)) {
                type = 2; // 下午：开始时间 > 12:00
            } else {
                type = 1; // 上午：其他情况（包括结束时间 <= 12:00）
            }

            // 添加结果
            spareDateVos.add(new IcSpareTimeVo.SpareDateVo(date, type));
        }

        spareTimeVo.setSpareDateVos(spareDateVos);
    }

    private void singleSkipHoliday(IcSpareTimeVo spareTimeVo) {

    }


    // 假设的节假日判断方法，返回 true 表示是节假日
    private boolean isHoliday(LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        Boolean exists = jedisClient.exists(dateStr);
        if(exists){
            jedisClient.expire(dateStr, EXPIRE_TIME);
            return true;
        }
        String url = HOLIDAY_API_URL + dateStr;
        // 使用 Hutool 的 HttpUtil 发送 GET 请求
        String jsonResponse = HttpUtil.get(url);

        // 解析 JSON
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        int type = jsonObject.getAsJsonObject("type").get("type").getAsInt();

        //0: 工作日
        //1: 节假日
        //2: 调休工作日
        //3: 周末
        if(1 == type){
            jedisClient.set(dateStr, dateStr, EXPIRE_TIME);
            return true;
        }
        return false;
    }

}
