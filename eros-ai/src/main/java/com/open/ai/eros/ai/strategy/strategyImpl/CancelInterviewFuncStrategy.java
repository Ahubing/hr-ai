package com.open.ai.eros.ai.strategy.strategyImpl;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.manager.ICAiManager;
import com.open.ai.eros.ai.strategy.FuncCallStrategy;
import com.open.ai.eros.common.constants.InterviewRoleEnum;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.vo.ResultVO;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class CancelInterviewFuncStrategy implements FuncCallStrategy {


    @Autowired
    private ICAiManager icAiManager;
    @Override
    public Boolean supports(String toolName) {
        return "cancel_interview".equals(toolName);
    }

    @Override
    public ToolExecutionResultMessage handle(AtomicInteger statusCode, AtomicInteger needToReply,
                                             AtomicBoolean isAiSetStatus, JSONObject preParams,
                                             String aiCallResult, ToolExecutionRequest toolExecutionRequest) {
        statusCode.set(ReviewStatusEnums.INVITATION_FOLLOW_UP.getStatus());
        String interviewId = preParams.getString("interviewId");
        ResultVO<Boolean> resultVO = icAiManager.cancelInterview(interviewId, InterviewRoleEnum.EMPLOYEE.getCode());

        log.info("取消面试: tool={}, cancelInterviewStr={}", toolExecutionRequest.name(), JSONObject.toJSONString(resultVO));
        return ToolExecutionResultMessage.from(toolExecutionRequest, JSONObject.toJSONString(resultVO));
    }
}
