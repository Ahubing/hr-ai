package com.open.ai.eros.ai.strategy.strategyImpl;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.manager.ICAiManager;
import com.open.ai.eros.ai.strategy.FuncCallStrategy;
import com.open.ai.eros.common.constants.InterviewRoleEnum;
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
public class ModifyTimeFuncStrategy implements FuncCallStrategy {

    @Autowired
    private ICAiManager icAiManager;

    @Override
    public Boolean supports(String toolName) {
        return "modify_interview_time".equals(toolName);
    }

    @Override
    public ToolExecutionResultMessage handle(AtomicInteger statusCode, AtomicInteger needToReply,
                                             AtomicBoolean isAiSetStatus, JSONObject preParams,
                                             String aiCallResult, ToolExecutionRequest toolExecutionRequest) {
        String interviewId = preParams.getString("interviewId");
        JSONObject params = JSONObject.parseObject(aiCallResult);
        String newTime = params.getString("newTime");
        LocalDateTime sTime = LocalDateTime.parse(newTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResultVO<Boolean> resultVO = icAiManager.modifyTime(interviewId, InterviewRoleEnum.EMPLOYEE.getCode(),sTime);

        log.info("修改面试时间: tool={}, modifyTimeStr={}", toolExecutionRequest.name(), JSONObject.toJSONString(resultVO));
        return ToolExecutionResultMessage.from(toolExecutionRequest, JSONObject.toJSONString(resultVO));
    }
}
