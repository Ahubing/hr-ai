package com.open.ai.eros.ai.tool.function;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import com.open.ai.eros.ai.tool.tmp.ICTmpManager;
import com.open.ai.eros.ai.tool.tmp.tmpbean.IcRecordAddReq;
import com.open.ai.eros.ai.tool.tmp.tmpbean.IcSpareTimeReq;
import com.open.ai.eros.ai.tool.tmp.tmpbean.IcSpareTimeVo;
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
    private ICTmpManager icTmpManager;

    @Tool(name = "get_spare_time", value = {"查询可用面试时间。返回时间段内所有可面试时间"})
    public String get_spare_time(@P("开始时间（格式为yyyy-MM-ddTHH:mm:ss）") String startTime,
                                 @P("结束时间（格式为yyyy-MM-ddTHH:mm:ss）") String endTime,
                                 @P("当前角色的面具ID") String maskId) {
        log.info("get_spare_time function params startTime:{} endTime:{} maskId:{}", startTime, endTime, maskId);
        LocalDateTime sTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime eTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<IcSpareTimeVo> resultVO = icTmpManager.getSpareTime(new IcSpareTimeReq(Long.parseLong(maskId), sTime, eTime));
        return JSONUtil.toJsonStr(resultVO.getData());
    }

    @Tool(name = "appoint_interview", value = {"为求职者预约面试时间。并告知求职者面试id，方便后续进行修改面试时间或取消面试。"})
    public String appoint_interview(@P("maskId") String maskId,
                                    @P("adminId") String adminId,
                                    @P("employeeUid") String employeeUid,
                                    @P("求职者期望的面试开始时间（格式为yyyy-MM-ddTHH:mm:ss）") String startTime,
                                    @P("positionId") String positionId,
                                    @P("accountId") String accountId) {
        log.info("appoint_interview function params maskId:{} adminId:{} employeeUid:{} startTime:{} positionId:{} accountId:{}", maskId, adminId, employeeUid, startTime, positionId, accountId);
        LocalDateTime sTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<String> resultVO = icTmpManager.appointInterview(new IcRecordAddReq(Long.parseLong(maskId),Long.parseLong(adminId),employeeUid,sTime,Long.parseLong(positionId),accountId));
        return JSONUtil.toJsonStr(resultVO);
    }

    @Tool(name = "cancel_interview", value = {"取消面试"})
    public String cancel_interview(@P("面试的id") String interviewId) {
        log.info("cancel_interview function params interviewId:{}", interviewId);
        ResultVO<Boolean> resultVO = icTmpManager.cancelInterview(interviewId,2);
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("result",200 == resultVO.getCode() ? "success":resultVO.getMsg());
        return JSONUtil.toJsonStr(jsonObject);
    }
    @Tool(name = "modify_interview_time", value = {"修改面试时间"})
    public String modify_interview_time(@P("原面试的id") String interviewId,
                                        @P("修改到的新时间（格式为yyyy-MM-ddTHH:mm:ss）") String newTime) {
        log.info("modify_interview_time function params interviewId:{} newTime:{}", interviewId, newTime);
        LocalDateTime sTime = LocalDateTime.parse(newTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<Boolean> resultVO = icTmpManager.modifyTime(interviewId,sTime);
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("result",200 == resultVO.getCode() ? "success":resultVO.getMsg());
        return JSONUtil.toJsonStr(jsonObject);
    }
    private String buildSpareTimeResponse(IcSpareTimeVo data) {
        List<IcSpareTimeVo.SpareDateVo> dateVos = data.getSpareDateVos();
        if (CollectionUtil.isNotEmpty(dateVos)) {
            StringBuilder sb = new StringBuilder();
            sb.append("我大概看了一下，我在这几个时间段是空闲的:\n");
            for (IcSpareTimeVo.SpareDateVo dateVo : dateVos) {
                LocalDate date = dateVo.getLocalDate();
                int monthValue = date.getMonthValue();
                int dayOfMonth = date.getDayOfMonth();
                sb.append(monthValue).append("月").append(dayOfMonth).append("日").append(":\n");
                for (IcSpareTimeVo.SparePeriodVo sparePeriodVo : dateVo.getSparePeriodVos()) {
                    LocalDateTime startTime = sparePeriodVo.getStartTime();
                    LocalDateTime endTime = sparePeriodVo.getEndTime();
                    int startHour = startTime.getHour();
                    int startMinute = startTime.getMinute();
                    int endHour = endTime.getHour();
                    int endMinute = endTime.getMinute();
                    sb.append(startHour).append("点").append(startMinute).append("分到").append(endHour).append("点").append(endMinute).append("分").append("\n");
                }
            }
            return sb.toString();
        }
        // TODO: 改成json描述
        return "抱歉，这个时间我有别的面试了，换个时间吧";
    }
}