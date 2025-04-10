package com.open.ai.eros.ai.strategy.strategyImpl;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.req.IcRecordAddReq;
import com.open.ai.eros.ai.manager.ICAiManager;
import com.open.ai.eros.ai.strategy.FuncCallStrategy;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.vo.ResultVO;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class AppointInterviewFuncStrategy implements FuncCallStrategy {

    @Autowired
    private ICAiManager icAiManager;

    @Override
    public Boolean supports(String toolName) {
        return "appoint_interview".equals(toolName);
    }

    @Override
    public ToolExecutionResultMessage handle(AtomicInteger statusCode, AtomicInteger needToReply,
                                             AtomicBoolean isAiSetStatus, JSONObject preParams,
                                             String aiCallResult, ToolExecutionRequest toolExecutionRequest) {
        statusCode.set(ReviewStatusEnums.INTERVIEW_ARRANGEMENT.getStatus());
        String adminId = preParams.getString("adminId");
        String employeeUid = preParams.getString("employeeUid");
        String positionId = preParams.getString("positionId");
        String accountId = preParams.getString("accountId");
        String maskId = preParams.getString("maskId");
        JSONObject params = JSONObject.parseObject(aiCallResult);
        String startTime = params.getString("startTime");
        LocalDateTime sTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<String> resultVO = icAiManager.appointInterview(new IcRecordAddReq(Long.parseLong(maskId), Long.parseLong(adminId), employeeUid, sTime, Long.parseLong(positionId), accountId));
        log.info("预约面试: tool={}, appointInterviewStr={}", toolExecutionRequest.name(), JSONObject.toJSONString(resultVO));
        return ToolExecutionResultMessage.from(toolExecutionRequest, JSONObject.toJSONString(resultVO));
    }
}
