package com.open.ai.eros.ai.strategy.strategyImpl;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.strategy.FuncCallStrategy;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
@Component
@Slf4j
public class SetStatusFuncStrategy implements FuncCallStrategy {

    @Override
    public Boolean supports(String toolName) {
        return "set_status".equals(toolName);
    }

    @Override
    public ToolExecutionResultMessage handle(AtomicInteger statusCode, AtomicInteger needToReply,
                                             AtomicBoolean isAiSetStatus, JSONObject preParams,
                                             String aiCallResult, ToolExecutionRequest toolExecutionRequest) {
        ReviewStatusEnums enums = ReviewStatusEnums.getEnumByKey(aiCallResult);
        if (Objects.nonNull(enums)) {
            statusCode.set(enums.getStatus());
            isAiSetStatus.set(Boolean.TRUE);
            log.info("状态已更新: tool={}, aDefault={},status={}", toolExecutionRequest.name(), enums.getDesc(), aiCallResult);
        }
        return ToolExecutionResultMessage.from(toolExecutionRequest, aiCallResult);
    }
}
