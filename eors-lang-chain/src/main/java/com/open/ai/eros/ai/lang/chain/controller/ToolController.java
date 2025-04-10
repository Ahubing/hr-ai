package com.open.ai.eros.ai.lang.chain.controller;

import com.open.ai.eros.ai.lang.chain.bean.ToolSimpleVo;
import com.open.ai.eros.ai.lang.chain.config.AiToolBaseController;
import com.open.ai.eros.ai.tool.config.ToolConfig;
import com.open.ai.eros.common.vo.ResultVO;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @类名：ToolController
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/15 9:35
 */

@RestController
public class ToolController extends AiToolBaseController {


    /**
     * 获取ai的工具
     *
     * @return
     */
    @GetMapping("/ai/tool")
    public ResultVO<List<ToolSimpleVo>> getToolList() {
        List<ToolSimpleVo> toolSimpleVos = new ArrayList<>();
        Map<String, ToolSpecification> methodMap = ToolConfig.methodMap;
        Set<String> methods = methodMap.keySet();
        for (String method : methods) {
            toolSimpleVos.add(new ToolSimpleVo(method, method));
        }
        return ResultVO.success(toolSimpleVos);
    }


    @GetMapping("/ai/tool/test")
    public ResultVO testTool(@RequestParam(value = "name") String name) {
        Map<String, ToolSpecification> methodMap = ToolConfig.methodMap;
        ToolSpecification toolSpecification = methodMap.get(name);
        if (toolSpecification == null) {
            return ResultVO.fail("未发现tool");
        }
        Map<ToolSpecification, DefaultToolExecutor> toolExecutorMap = ToolConfig.toolExecutorMap;
        DefaultToolExecutor defaultToolExecutor = toolExecutorMap.get(toolSpecification);
        if (defaultToolExecutor == null) {
            return ResultVO.fail("未发现执行类");
        }

        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                .baseUrl("https://zen-vip.zeabur.app/v1")
                .modelName("gpt-4o-2024-05-13")
                .build();

        List<ChatMessage> messages = new ArrayList<>();
        String userMessage = "输出明天下午两点的时间?";
        messages.add(new UserMessage("user", userMessage));
        Response<AiMessage> generate = model.generate(messages, Arrays.asList(toolSpecification));
        AiMessage content = generate.content();
        List<ToolExecutionRequest> toolExecutionRequests = content.toolExecutionRequests();
        for (ToolExecutionRequest toolExecutionRequest : toolExecutionRequests) {
            String aDefault = defaultToolExecutor.execute(toolExecutionRequest, "default");
            System.out.println(aDefault);
            if (StringUtils.isNoneEmpty(aDefault)) {
                return ResultVO.success(aDefault);
            }
        }
        return ResultVO.success();
    }


}
