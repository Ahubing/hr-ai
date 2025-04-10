package com.open.ai.eros.ai.strategy;

import com.alibaba.fastjson.JSONObject;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.ToolExecutionResultMessage;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public interface FuncCallStrategy {

    Boolean supports(String toolName);

    ToolExecutionResultMessage handle(AtomicInteger statusCode, AtomicInteger needToReply,
                                      AtomicBoolean isAiSetStatus , JSONObject preParams,
                                      String aiCallResult, ToolExecutionRequest toolExecutionRequest);
}
