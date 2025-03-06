package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.open.hr.ai.bean.vo.SlackOffVo;
import com.open.hr.ai.bean.vo.WorkTimeVo;

import java.util.List;

public class TimeToDecimalConverter {

    /**
     * 将标准时间格式（HH:mm）转换为小数格式的时间
     *
     * @param time 标准时间格式的字符串（HH:mm）
     * @return 小数格式的时间
     */
    public static double convertTimeToDecimal(String time) {
        if (time == null || !time.matches("^([01]?\\d|2[0-4]):([0-5]\\d)$")) {
            throw new IllegalArgumentException("时间格式无效，必须为 HH:mm 且在 00:00 到 24:00 之间");
        }

        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        // 特殊处理 24:00 的情况
        if (hours == 24 && minutes == 0) {
            return 24.0;
        }

        // 转换为小数格式
        return hours + (minutes / 60.0);
    }
    public static void convertSlack(SlackOffVo slackOffVo,JSONObject jsonObject) {
        // 创建主 JSON 对象

        // 添加 slack_off 属性
        jsonObject.put("slack_off", slackOffVo.getSlackOff());

        // 创建 work_time 数组
        JSONArray workTimeArray = new JSONArray();
        List<WorkTimeVo> workTimeVoList = slackOffVo.getWorkTime();
        if (workTimeVoList != null) {
            for (WorkTimeVo workTimeVo : workTimeVoList) {
                JSONObject workTimeJson = new JSONObject();
                workTimeJson.put("start_time", convertTimeToDecimal(workTimeVo.getStartTime()));
                workTimeJson.put("end_time", convertTimeToDecimal(workTimeVo.getEndTime()));
                workTimeArray.add(workTimeJson);
            }
        }

        // 添加 work_time 数组到主 JSON 对象
        jsonObject.put("work_time", workTimeArray);
    }

    public static void main(String[] args) {
        // 测试示例
        String test = "{\"slackOff\":1,\"workTime\":[{\"startTime\":\"09:00\",\"endTime\":\"12:00\"},{\"startTime\":\"14:00\",\"endTime\":\"18:00\"}]}";
        JSONObject jsonObject = new JSONObject();
        SlackOffVo slackOffVo = JSONObject.parseObject(test, SlackOffVo.class);
        TimeToDecimalConverter.convertSlack(slackOffVo,jsonObject);
        System.out.println(jsonObject);
    }
}
