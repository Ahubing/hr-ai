package com.open.ai.eros.ai.strategy.strategyImpl;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.strategy.FuncCallStrategy;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
@Component
public class CheckReplyFuncStrategy implements FuncCallStrategy {

    @Override
    public Boolean supports(String toolName) {
        return "check_need_reply".equals(toolName);
    }

    @Override
    public ToolExecutionResultMessage handle(AtomicInteger statusCode, AtomicInteger needToReply,
                                             AtomicBoolean isAiSetStatus, JSONObject preParams,
                                             String aiCallResult, ToolExecutionRequest toolExecutionRequest) {
        needToReply.set(0);
        log.info("判断是否需要回复, tool={}, status={},needToReply={}", toolExecutionRequest.name(), aiCallResult, needToReply.get());
        return ToolExecutionResultMessage.from(toolExecutionRequest, aiCallResult);
    }
}
