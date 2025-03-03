package com.open.ai.eros.ai.manager;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.tool.config.ToolConfig;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.util.AIJsonUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.db.constants.AIRoleEnum;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @类名：CommonAIManager
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/12/6 11:56
 */
@Slf4j
@Component
public class CommonAIManager {


    @Autowired
    private ModelConfigManager modelConfigManager;


    /**
     * 非流获取ai结果
     *
     * @param templateModel
     * @param content
     * @return
     */
//    public ChatMessage aiNoStream(String templateModel, String content) {
//        return aiNoStream(Collections.singletonList(new ChatMessage(AIRoleEnum.USER.getRoleName(), content)), null, templateModel, 0.8);
//    }

    /**
     * 非流获取ai结果
     *
     * @param messages
     * @param templateModel
     * @param temperature
     * @return
     */
    public ChatMessage aiNoStream(List<ChatMessage> messages,
                                  List<String> tools,
                                  String templateModel,
                                  Double temperature, AtomicInteger statusCode ) {


        try {

            Map<String, ToolSpecification> methodMap = ToolConfig.methodMap;
            Map<ToolSpecification, DefaultToolExecutor> toolExecutorMap = ToolConfig.toolExecutorMap;


            Map<String,DefaultToolExecutor> executorMap = new HashMap<>();
            List<ToolSpecification> toolSpecifications = new ArrayList<>();
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
            //ModelConfigVo modelConfig = modelConfigManager.getModelConfig(templateModel);
            //if (modelConfig == null) {
            //    log.error("aiNoStream  error  无可用的渠道 templateModel={}", templateModel);
            //    throw new AIException("无可用的渠道  templateModel={} " + templateModel);
            //}
            String[] split = templateModel.split(":");

            List<dev.langchain4j.data.message.ChatMessage> newMessages = new ArrayList<>();
            for (ChatMessage message : messages) {
                if (message.getRole().equals(AIRoleEnum.SYSTEM.getRoleName())) {
                    SystemMessage systemMessage = new SystemMessage(message.getContent().toString());
                    newMessages.add(systemMessage);
                    continue;
                }
                if (message.getRole().equals(AIRoleEnum.USER.getRoleName())) {
                    UserMessage user = new UserMessage("user", message.getContent().toString());
                    newMessages.add(user);
                    continue;
                }
                if (message.getRole().equals(AIRoleEnum.ASSISTANT.getRoleName())) {
                    AiMessage aiMessage = new AiMessage(message.getContent().toString());
                    newMessages.add(aiMessage);
                }
            }

            String url = getUrl("https://bluecatai.net/");
            String token = "sk-7e3d932bef164aedb8f3a33a90a51e7f";

            OpenAiChatModel modelService = OpenAiChatModel.builder()
                    .apiKey(token)
                    .baseUrl(url)
                    .modelName(split[1])
                    .temperature(temperature)
                    .build();
            Response<AiMessage> generate = modelService.generate(newMessages, toolSpecifications);
            AiMessage content = generate.content();
            List<ToolExecutionRequest> toolExecutionRequests = content.toolExecutionRequests();
            StringBuilder sb = new StringBuilder();//收集所有需要让ai回复的值
            List<ToolExecutionResultMessage> toolResults = new ArrayList<>(); // 收集工具结果

            if (CollectionUtils.isNotEmpty(toolExecutionRequests)) {
                for (ToolExecutionRequest toolExecutionRequest : toolExecutionRequests) {
                    String name = toolExecutionRequest.name();
                    log.info("正在执行工具: tool={}, arguments={}", name, toolExecutionRequest.arguments());
                    try {
                        DefaultToolExecutor executor = executorMap.get(name);
                        if (executor == null) {
                            continue;
                        }

                        // 执行工具时传递实际参数
                        String result = executor.execute(toolExecutionRequest, toolExecutionRequest.arguments());
                        log.info("执行工具结果: tool={}, result={}", name, result);

                        // 特殊业务逻辑
                        switch (name) {
                            case "set_status":
                                ReviewStatusEnums enums = ReviewStatusEnums.getEnumByKey(result);
                                statusCode.set(enums.getStatus());
                                log.info("状态已更新: tool={}, aDefault={},status={}", name, enums.getDesc(), result);
                                break;
                            case "get_spare_time":
                                toolResults.add(ToolExecutionResultMessage.from(toolExecutionRequest, result));
                                sb.append(result);
                                log.info("获取空闲时间: tool={}, spareTimeStr={}", name, result);
                                break;
                            case "appoint_interview":
                                toolResults.add(ToolExecutionResultMessage.from(toolExecutionRequest, result));
                                sb.append(result);
                                log.info("预约面试: tool={}, appointInterviewStr={}", name, result);
                                break;
                            case "cancel_interview":
                                toolResults.add(ToolExecutionResultMessage.from(toolExecutionRequest, result));
                                sb.append(result);
                                log.info("取消面试: tool={}, cancelInterviewStr={}", name, result);
                                break;
                            case "modify_interview_time":
                                toolResults.add(ToolExecutionResultMessage.from(toolExecutionRequest, result));
                                sb.append(result);
                                log.info("修改面试时间: tool={}, modifyTimeStr={}", name, result);
                        }
                    } catch (Exception e) {
                        log.error("工具执行失败: tool={}", name, e);
                   }
                }
            }

            log.info("toolResults={}", toolResults.isEmpty() ? "空的toolResults" : toolResults.toString());
            // 将工具结果反馈给模型并生成最终回答
            if (!toolResults.isEmpty()) {
                // 将工具结果添加到对话历史
                List<dev.langchain4j.data.message.ChatMessage> updatedMessages = new ArrayList<>(newMessages);
                updatedMessages.addAll(toolResults);

                // 生成最终回答
                Response<AiMessage> finalResponse = modelService.generate(updatedMessages);
                log.info("finalResponse text={}",finalResponse.content().text());
                return new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), finalResponse.content().text());
            }
            log.info("aiNoStream text={}",content.text());
            log.info("aiNoStream sb={}",sb.length());
            return new ChatMessage(
                    AIRoleEnum.ASSISTANT.getRoleName(),
                    sb.length() > 0 ? sb.toString() : content.text()
            );

        } catch (Exception e) {
            log.error("aiNoStream error templateModel={} ", templateModel, e);
        }
        return null;

    }


    /**
     * 非流获取ai结果,用于简历提取
     *
     * @param messages
     * @param templateModel
     * @param temperature
     * @return
     */
    public String aiNoStreamWithResume(List<ChatMessage> messages,
                                  String templateModel,
                                  Double temperature ) {

        try {
            String[] split = templateModel.split(":");
            List<dev.langchain4j.data.message.ChatMessage> newMessages = new ArrayList<>();
            for (ChatMessage message : messages) {
                if (message.getRole().equals(AIRoleEnum.SYSTEM.getRoleName())) {
                    SystemMessage systemMessage = new SystemMessage(message.getContent().toString());
                    newMessages.add(systemMessage);
                    continue;
                }
                if (message.getRole().equals(AIRoleEnum.USER.getRoleName())) {
                    UserMessage user = new UserMessage("user", message.getContent().toString());
                    newMessages.add(user);
                    continue;
                }
                if (message.getRole().equals(AIRoleEnum.ASSISTANT.getRoleName())) {
                    AiMessage aiMessage = new AiMessage(message.getContent().toString());
                    newMessages.add(aiMessage);
                }
            }

            String url = getUrl("https://one.opengptgod.com/");
            String token = "sk-YXkz1ruOVODeZXCG93F9847a6a784d749d2d1c2dCa3868Af";
            OpenAiChatModel modelService = OpenAiChatModel.builder()
                    .apiKey(token)
                    .baseUrl(url)
                    .modelName(split[1])
                    .temperature(temperature)
                    .build();
            Response<AiMessage> generate = modelService.generate(newMessages);
            AiMessage content = generate.content();
            return content.text();
        } catch (Exception e) {
            log.error("aiNoStream error templateModel={} ", templateModel, e);
        }
        return null;

    }




    /**
     * 非流获取ai结果
     *
     * @param messages
     * @param templateModel
     * @param temperature
     * @return
     */
    public String aiNoStreamWith(List<ChatMessage> messages,
                                       String templateModel,
                                       Double temperature ) {

        try {
            String[] split = templateModel.split(":");
            List<dev.langchain4j.data.message.ChatMessage> newMessages = new ArrayList<>();
            for (ChatMessage message : messages) {
                if (message.getRole().equals(AIRoleEnum.SYSTEM.getRoleName())) {
                    SystemMessage systemMessage = new SystemMessage(message.getContent().toString());
                    newMessages.add(systemMessage);
                    continue;
                }
                if (message.getRole().equals(AIRoleEnum.USER.getRoleName())) {
                    UserMessage user = new UserMessage("user", message.getContent().toString());
                    newMessages.add(user);
                    continue;
                }
                if (message.getRole().equals(AIRoleEnum.ASSISTANT.getRoleName())) {
                    AiMessage aiMessage = new AiMessage(message.getContent().toString());
                    newMessages.add(aiMessage);
                }
            }


            String url = getUrl("https://bluecatai.net/");
            String token = "sk-7e3d932bef164aedb8f3a33a90a51e7f";
            OpenAiChatModel modelService = OpenAiChatModel.builder()
                    .apiKey(token)
                    .baseUrl(url)
                    .modelName(split[1])
                    .temperature(temperature)
                    .build();
            Response<AiMessage> generate = modelService.generate(newMessages);
            AiMessage content = generate.content();
            return content.text();
        } catch (Exception e) {
            log.error("aiNoStream error templateModel={} ", templateModel, e);
        }
        return null;

    }

    public static String getUrl(String cdnHost) {
        return cdnHost.endsWith("/") ? cdnHost + "v1" : cdnHost + "/v1";
    }



    private static String name = "setStatus";

    public DefaultToolExecutor getToolExecutor(List<String> tools) {

        List<DefaultToolExecutor> defaultToolExecutors = new ArrayList<>();
        Map<String, ToolSpecification> methodMap = ToolConfig.methodMap;
        ToolSpecification toolSpecification = methodMap.get(name);
        Map<ToolSpecification, DefaultToolExecutor> toolExecutorMap = ToolConfig.toolExecutorMap;
        return toolExecutorMap.get(toolSpecification);
    }

//    public static void main(String[] args) {
//        String url = getUrl("https://one.opengptgod.com/");
//        String token = "sk-YXkz1ruOVODeZXCG93F9847a6a784d749d2d1c2dCa3868Af";
//
//        OpenAiChatModel modelService = OpenAiChatModel.builder()
//                .apiKey(token)
//                .baseUrl(url)
//                .modelName("gpt-4o-all")
//                .temperature(0.8)
//                .build();
//        List<dev.langchain4j.data.message.ChatMessage> newMessages = new ArrayList<>();
//        SystemMessage systemMessage = new SystemMessage("你是一名专业的数据提取工程师,擅长从简历中提取出数据,并且根据用户的要求提取json格式的数据\n\n 要求不要输出与json数据无关的内容. 需要提取的json结构数据如下,未明确的json字段则该字段的内容直接输出null, 以可以解析成json的格式输出给我,请严格按照下面的格式 \n\n ```json{\"name\":\"姓名\",\"company\":\"所在公司\",\"city\":\"所在城市\",\"position\":\"应聘岗位\",\"gender\":\"性别\",\"salary\":\"工资\",\"education\":[{\"school\":\"陕西开放大学\",\"major\":\"电气自动化技术\",\"degree\":\"高中|大专|大学|硕士|博士 \",\"time_range\":\"2022 - 2024\"}],\"age\":20,\"experiences\":[{\"company\":\"卓望信息科技有限公司\",\"position\":\"营运推广主管\",\"startDate\":\"2013.10\",\"endDate\":\"至今\",\"responsibilities\":[\"负责社会化搭建工作\"],\"achievements\":[\"社会化媒体账号\"]}],\"projects\":[{\"name\":\"stm32\",\"role\":\"自主编写\",\"time_range\":\"2025.02 - 2025.02 \",\"description\":\"项目描述\"}],\"applyStatus\":\"应聘状态,比如: 在校-考虑机会\",\"phone\":\"手机号\",\"wechat\":\"微信号\",\"email\":\"邮箱号\",\"workYears\":\"工作年限\",\"skills\":\"技能\"}```");
//        newMessages.add(systemMessage);
//        UserMessage user = new UserMessage("user", "https://www.quwen.chat/2025-02-15/67aa6feacdb64aa994f79f88e3974c31.docx  你好帮我分析这个文档,提取出简历信息, 不要输出其他无关的数据");
//        newMessages.add(user);
//
//        Response<AiMessage> generate = modelService.generate(newMessages);
//        AiMessage content = generate.content();
//        String text = content.text();
//        System.out.println(text);
//
//        String jsonContent = AIJsonUtil.getJsonContent(text);
//        if (StringUtils.isNotBlank(jsonContent)) {
//            JSONObject jsonObject = JSONObject.parseObject(jsonContent);
//            System.out.println(jsonObject);
//        }
//    }

}
