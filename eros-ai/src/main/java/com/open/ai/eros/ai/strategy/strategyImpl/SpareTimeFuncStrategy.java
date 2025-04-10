package com.open.ai.eros.ai.strategy.strategyImpl;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.req.IcSpareTimeReq;
import com.open.ai.eros.ai.bean.vo.IcSpareTimeVo;
import com.open.ai.eros.ai.manager.ICAiManager;
import com.open.ai.eros.ai.strategy.FuncCallStrategy;
import com.open.ai.eros.common.vo.ResultVO;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class SpareTimeFuncStrategy implements FuncCallStrategy {

    @Autowired
    private ICAiManager icAiManager;

    @Override
    public Boolean supports(String toolName) {
        return "get_spare_time".equals(toolName);
    }

    @Override
    public ToolExecutionResultMessage handle(AtomicInteger statusCode, AtomicInteger needToReply,
                                             AtomicBoolean isAiSetStatus, JSONObject preParams,
                                             String aiCallResult, ToolExecutionRequest toolExecutionRequest) {
        String maskId = preParams.getString("maskId");
        JSONObject params = JSONObject.parseObject(aiCallResult);
        String startTime = params.getString("startTime");
        String endTime = params.getString("endTime");
        LocalDateTime sTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime eTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ToolExecutionResultMessage resultMessage = null;
        ResultVO<IcSpareTimeVo> resultVO = icAiManager.getSpareTime(new IcSpareTimeReq(Long.parseLong(maskId), sTime, eTime));
        if(200 == resultVO.getCode()){
            if (CollectionUtils.isEmpty(resultVO.getData().getSpareDateVos())) {
                resultMessage = ToolExecutionResultMessage.from(toolExecutionRequest, JSONObject.toJSONString(ResultVO.fail(500,"无空闲时间")));
            }
        }
        if(resultMessage == null){
            resultMessage = ToolExecutionResultMessage.from(toolExecutionRequest, JSONObject.toJSONString(resultVO));
        }
        log.info("获取空闲时间: tool={}, spareTimeStr={}", toolExecutionRequest.name(), JSONObject.toJSONString(resultVO));
        return resultMessage;
    }
}
