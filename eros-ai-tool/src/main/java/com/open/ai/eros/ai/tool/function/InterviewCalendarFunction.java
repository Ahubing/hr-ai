package com.open.ai.eros.ai.tool.function;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.tool.tmp.ICManager;
import com.open.ai.eros.ai.tool.tmp.IcRecordAddReq;
import com.open.ai.eros.ai.tool.tmp.IcSpareTimeReq;
import com.open.ai.eros.ai.tool.tmp.IcSpareTimeVo;
import com.open.ai.eros.common.vo.ResultVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class InterviewCalendarFunction {

    @Resource
    private ICManager icTmpManager;

    @Tool(name = "get_spare_time", value = {"查询可用面试时间。返回时间段内所有可面试时间"})
    public String get_spare_time(@P("开始时间（格式为yyyy-MM-ddTHH:mm:ss）") String startTime,
                                 @P("结束时间（格式为yyyy-MM-ddTHH:mm:ss）") String endTime,
                                 @P("当前角色的面具ID") String maskId) {
        log.info("get_spare_time function params startTime:{} endTime:{} maskId:{}", startTime, endTime, maskId);
        LocalDateTime sTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime eTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<IcSpareTimeVo> resultVO = icTmpManager.getSpareTime(new IcSpareTimeReq(Long.parseLong(maskId), sTime, eTime));
        log.info("get_spare_time result:" + JSONObject.toJSONString(resultVO));
        return JSONObject.toJSONString(resultVO.getData());
    }

    @Tool(name = "appoint_interview", value = {"为求职者预约面试时间。"})
    public String appoint_interview(@P("maskId") String maskId,
                                    @P("adminId") String adminId,
                                    @P("employeeUid") String employeeUid,
                                    @P("求职者期望的面试开始时间（格式为yyyy-MM-ddTHH:mm:ss）") String startTime,
                                    @P("positionId") String positionId,
                                    @P("accountId") String accountId) {
        log.info("appoint_interview function params maskId:{} adminId:{} employeeUid:{} startTime:{} positionId:{} accountId:{}", maskId, adminId, employeeUid, startTime, positionId, accountId);
        LocalDateTime sTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<String> resultVO = icTmpManager.appointInterview(new IcRecordAddReq(Long.parseLong(maskId),Long.parseLong(adminId),employeeUid,sTime,Long.parseLong(positionId),accountId));
        log.info("appoint_interview result:" + JSONObject.toJSONString(resultVO));
        return JSONObject.toJSONString(resultVO);
    }

    @Tool(name = "cancel_interview", value = {"取消面试"})
    public String cancel_interview(@P("面试的id") String interviewId) {
        log.info("cancel_interview function params interviewId:{}", interviewId);
        ResultVO<Boolean> resultVO = icTmpManager.cancelInterview(interviewId,2);
        log.info("cancel_interview result:" + JSONObject.toJSONString(resultVO));
        return JSONObject.toJSONString(resultVO);
    }
    @Tool(name = "modify_interview_time", value = {"修改面试时间"})
    public String modify_interview_time(@P("原面试的id") String interviewId,
                                        @P("修改到的新时间（格式为yyyy-MM-ddTHH:mm:ss）") String newTime) {
        log.info("modify_interview_time function params interviewId:{} newTime:{}", interviewId, newTime);
        LocalDateTime sTime = LocalDateTime.parse(newTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<Boolean> resultVO = icTmpManager.modifyTime(interviewId,sTime);
        log.info("modify_interview_time result:" + JSONObject.toJSONString(resultVO));
        return JSONObject.toJSONString(resultVO);
    }

}