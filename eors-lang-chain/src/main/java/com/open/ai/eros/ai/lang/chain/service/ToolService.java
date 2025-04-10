package com.open.ai.eros.ai.lang.chain.service;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.lang.chain.bean.TokenUsageVo;
import com.open.ai.eros.ai.lang.chain.bean.UseToolResult;
import com.open.ai.eros.ai.lang.chain.provider.ModelConfigService;
import com.open.ai.eros.ai.tool.config.ToolConfig;
import com.open.ai.eros.common.constants.ModelPriceEnum;
import com.open.ai.eros.common.vo.ResultVO;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @类名：ToolService
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/15 16:38
 */

@Slf4j
@Component
public class ToolService {


    private static Set<String> supportToolModel = new HashSet<>();

    static {
        // 目前只对接了gpt支持函数调用
        supportToolModel.add(ModelPriceEnum.gpt_4o_2024_05_13.getModel());
        supportToolModel.add(ModelPriceEnum.gpt_4o_2024_08_06.getModel());
        supportToolModel.add(ModelPriceEnum.gpt_4o.getModel());
        supportToolModel.add(ModelPriceEnum.GPT_3_TURBO_16K.getModel());
    }


    @Autowired
    private ModelConfigService modelConfigService;


    /**
     * 使用 ai工具回调
     *
     * @param userMessage
     * @param tools
     * @return
     */
    public ResultVO<UseToolResult> useTool(String userMessage, List<String>tools,String template,String modelName){

        Map<String,DefaultToolExecutor> executorMap = new HashMap<>();
        List<ToolSpecification> toolSpecifications = new ArrayList<>();
        fullToolExecutorInfo(tools,executorMap,toolSpecifications);

        UseToolResult result = new UseToolResult();
        ModelConfigVo modelConfig = modelConfigService.getModelConfig(String.format("%s:%s", template, modelName));
        if(!supportToolModel.contains(modelName) || modelConfig==null ){
            return ResultVO.success(result);
        }

        List<ChatMessage> newMessages = new ArrayList<>();
        result.setChatMessages(newMessages);

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(modelConfig.getToken())
                .baseUrl(getUrl(modelConfig.getBaseUrl()))
                .modelName(modelName)
                .build();

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new UserMessage("user",userMessage));
        Response<AiMessage> generate = model.generate(messages, toolSpecifications);
        AiMessage content = generate.content();
        TokenUsage tokenUsage = generate.tokenUsage();

        TokenUsageVo tokenUsageVo = new TokenUsageVo();
        tokenUsageVo.setModel(modelName);
        tokenUsageVo.setInputTokenCount(tokenUsage.inputTokenCount());
        tokenUsageVo.setOutputTokenCount(tokenUsage.outputTokenCount());
        tokenUsageVo.setTotalTokenCount(tokenUsage.totalTokenCount());

        result.setTokenUsage(tokenUsageVo);
        if(!content.hasToolExecutionRequests()){
            log.info("useTool 没有发现工具 userMessage={},tool={} ",userMessage, JSONObject.toJSONString(tools));
            return ResultVO.success(result);
        }

        List<ChatMessage> toolResults = new ArrayList<>();

        List<ToolExecutionRequest> toolExecutionRequests = content.toolExecutionRequests();
        for (ToolExecutionRequest toolExecutionRequest : toolExecutionRequests) {
            // tool的名称
            String name = toolExecutionRequest.name();
            try {
                DefaultToolExecutor defaultToolExecutor = executorMap.get(name);
                if(defaultToolExecutor==null){
                    continue;
                }
                String aDefault = defaultToolExecutor.execute(toolExecutionRequest, "default");
                log.info("useTool tool={},aDefault={}",name,aDefault);

                if(StringUtils.isNoneEmpty(aDefault)){
                    ToolExecutionResultMessage toolExecutionResultMessage = ToolExecutionResultMessage.from(toolExecutionRequest, aDefault);
                    toolResults.add(toolExecutionResultMessage);
                }
            }catch (Exception e){
                log.error("useTool error toolName={}",name,e);
            }
        }
        if(CollectionUtils.isNotEmpty(toolResults)){
            newMessages.add(content);
            newMessages.addAll(toolResults);
        }
        return ResultVO.success(result);
    }




    public String getUrl(String cdnHost) {
        return cdnHost.endsWith("/") ? cdnHost + "v1" : cdnHost + "/v1";
    }

    public static void fullToolExecutorInfo(List<String> tools, Map<String,DefaultToolExecutor> executorMap,
                                            List<ToolSpecification> toolSpecifications) {
        Map<String, ToolSpecification> methodMap = ToolConfig.methodMap;
        Map<ToolSpecification, DefaultToolExecutor> toolExecutorMap = ToolConfig.toolExecutorMap;
        for (String tool : tools) {
            ToolSpecification toolSpecification = methodMap.get(tool);
            if(toolSpecification==null){
                log.error("未发现 tool ={}",tool);
                continue;
            }
            DefaultToolExecutor defaultToolExecutor = toolExecutorMap.get(toolSpecification);
            if(defaultToolExecutor==null){
                log.error("未发现 tool功能提供者 ={}",tool);
                continue;
            }
            toolSpecifications.add(toolSpecification);
            executorMap.put(toolSpecification.name(),defaultToolExecutor);
        }
    }

}
