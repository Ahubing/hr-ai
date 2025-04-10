package com.open.ai.eros.ai.processor;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.strategy.FuncCallStrategy;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.ToolExecutionResultMessage;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FuncCallProcessor {

    public static final Collection<FuncCallStrategy> funcCallStrategies = SpringUtil.getApplicationContext().getBeansOfType(FuncCallStrategy.class).values();
    public static ToolExecutionResultMessage process(ToolExecutionRequest toolExecutionRequest, AtomicInteger statusCode,
                                                     AtomicInteger needToReply, AtomicBoolean isAiSetStatus,
                                                     JSONObject preParams, String aiCallResult) {
        for (FuncCallStrategy funcCallStrategy : funcCallStrategies) {
            if (funcCallStrategy.supports(toolExecutionRequest.name())) {
                return funcCallStrategy.handle(statusCode, needToReply, isAiSetStatus, preParams,aiCallResult,toolExecutionRequest);
            }
        }
        return null;
    }
}
