package com.open.ai.eros.ai.tool.function;

import com.alibaba.fastjson.JSONObject;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.server.Jsp;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InterviewCalendarFunction {

    @Tool(name = "get_spare_time", value = {"查询可用面试时间。返回时间段内所有可面试时间"})
    public String get_spare_time(@P("开始时间（格式为yyyy-MM-ddTHH:mm:ss）") String startTime,
                                 @P("结束时间（格式为yyyy-MM-ddTHH:mm:ss）") String endTime,
                                 @P("当前角色的面具ID") String maskId) {
        log.info("get_spare_time function params startTime:{} endTime:{} maskId:{}", startTime, endTime, maskId);
        JSONObject params = new JSONObject();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("maskId", maskId);
        return JSONObject.toJSONString(params);
    }

    @Tool(name = "appoint_interview", value = {"为求职者预约面试时间"})
    public String appoint_interview(@P("maskId") String maskId,
                                    @P("adminId") String adminId,
                                    @P("employeeUid") String employeeUid,
                                    @P("求职者期望的面试开始时间（格式为yyyy-MM-ddTHH:mm:ss）") String startTime,
                                    @P("positionId") String positionId,
                                    @P("accountId") String accountId) {
        log.info("appoint_interview function params maskId:{} adminId:{} employeeUid:{} startTime:{} positionId:{} accountId:{}", maskId, adminId, employeeUid, startTime, positionId, accountId);
        JSONObject params = new JSONObject();
        params.put("maskId", maskId);
        params.put("adminId", adminId);
        params.put("employeeUid", employeeUid);
        params.put("startTime", startTime);
        params.put("positionId", positionId);
        params.put("accountId", accountId);
        return JSONObject.toJSONString(params);
    }

    @Tool(name = "cancel_interview", value = {"取消面试"})
    public String cancel_interview(@P("面试的id") String interviewId) {
        log.info("cancel_interview function params interviewId:{}", interviewId);
        JSONObject params = new JSONObject();
        params.put("interviewId", interviewId);
        return JSONObject.toJSONString(params);
    }

    @Tool(name = "modify_interview_time", value = {"修改面试时间"})
    public String modify_interview_time(@P("原面试的id") String interviewId,
                                        @P("修改到的新时间（格式为yyyy-MM-ddTHH:mm:ss）") String newTime) {
        log.info("modify_interview_time function params interviewId:{} newTime:{}", interviewId, newTime);
        JSONObject params = new JSONObject();
        params.put("interviewId", interviewId);
        params.put("newTime", newTime);
        return JSONObject.toJSONString(params);
    }

}