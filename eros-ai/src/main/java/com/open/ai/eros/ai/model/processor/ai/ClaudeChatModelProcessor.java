//package com.open.ai.eros.ai.processor;
//
//import com.alibaba.fastjson.JSONObject;
//import com.blue.cat.common.constants.BaseCodeEnum;
//import com.blue.cat.common.constants.ModelTemplateEnum;
//import com.blue.cat.common.util.RedisKeyUtil;
//import com.blue.cat.common.util.TokenUtil;
//import com.blue.cat.common.vo.ChatBaseMessage;
//import com.blue.cat.common.vo.ModelRelocation;
//import com.blue.cat.common.vo.NoServiceErrorException;
//import com.blue.cat.model.service.bean.dto.CatchOpenAiHttpException;
//import com.blue.cat.model.service.gpt.ChatBaseOpenAiProxyService;
//import com.blue.cat.model.service.gpt.OpenAiProxyServiceFactory;
//import com.blue.cat.model.service.model.ChatModelProcessor;
//import com.blue.cat.model.service.model.bean.ModelProcessorRequest;
//import com.blue.cat.model.service.redis.impl.ServiceJedisClientImplService;
//import com.blue.cat.model.service.util.claude.ClaudeCompletionResult;
//import com.blue.cat.model.service.util.claude.ClaudeMessage;
//import com.blue.cat.model.service.util.claude.ClaudeStreamCompletionChunk;
//import com.blue.cat.model.service.vo.*;
//import com.fasterxml.jackson.databind.exc.MismatchedInputException;
//import com.theokanning.openai.completion.chat.ChatMessageRole;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.*;
//import org.apache.catalina.connector.ClientAbortException;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.*;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//
//
///**
// * 适配所有的官方的claude模型的服务
// * 入参是 claude
// */
//@Slf4j
//@Component
//public class ClaudeChatModelProcessor extends BaseModelProcessor{
//
//
//
//    @Override
//    public ChatMessageResultVo startAIModel(ModelProcessorRequest modelProcessorRequest, OutputStream userOs, ModelConfigVo modelConfigVo) throws IOException {
//
//        ClaudeCompletionRequest claudeCompletionRequest = modelProcessorRequest.getClaudeCompletionRequest();
//
//        String model = claudeCompletionRequest.getModel();
//        ChatBaseOpenAiProxyService proxyService = OpenAiProxyServiceFactory.getProxyService(modelConfigVo);
//        if (proxyService == null) {
//            userOs.write(BaseCodeEnum.NO_MODEL.getMsg().getBytes(Charset.defaultCharset()));
//            throw new NoServiceErrorException("获取ChatBaseOpenAiProxyService 为空,请检查是否异常:modelId="+modelConfigVo.getId()+",model="+claudeCompletionRequest.getModel());
//        }
//        long currentTime = System.currentTimeMillis();
//        AtomicReference<Long> firstRTime = new AtomicReference<>();
//
//        LinkedList<ChatBaseMessage> messages = claudeCompletionRequest.getMessages();
//
//        int i = 0 ;
//        for (ChatBaseMessage message : messages) {
//            if(i%2==1){
//                message.setRole(ChatMessageRole.ASSISTANT.value());
//            }else{
//                message.setRole(ChatMessageRole.USER.value());
//            }
//            i++;
//        }
//
//        int promptTokenNumber = TokenUtil.countTokenMessages(messages, claudeCompletionRequest.getModel());
//        String url = getUrl(proxyService.getBaseUrl());
//        List<ClaudeStreamCompletionChunk> chunks = new ArrayList<>();
//        String source = claudeCompletionRequest.getSource();
//        String chatRequestJson = checkIsRelocation(claudeCompletionRequest, modelConfigVo);
//        claudeCompletionRequest.setModel(model);
//        try {
//            AtomicBoolean flag = new AtomicBoolean(false);
//            AtomicBoolean first = new AtomicBoolean(false);
//
//            // application/json; charset=utf-8
//            RequestBody body =  RequestBody.create(MediaType.parse("application/json; charset=utf-8"),chatRequestJson );
//            Request.Builder builder = new Request.Builder().url(url).method("POST", body)
//                    .addHeader("anthropic-version", "2023-06-01")
//                    .addHeader("x-api-key",   proxyService.token);
//
//            Request request = builder.build();
//            Response execute = proxyService.client.newCall(request).execute();
//            super.checkException(execute);
//            ResponseBody responseBody = execute.body();
//            if(!claudeCompletionRequest.getStream()){
//                //非流式的
//                String aiResult = responseBody.string();
//                if(StringUtils.isNoneEmpty(aiResult)){
//                    ClaudeCompletionResult completionResult = JSONObject.parseObject(aiResult, ClaudeCompletionResult.class);
//                    completionResult.setModel(model);
//                    ClaudeMessage chatMessage = new ClaudeMessage("text", completionResult.getContent().stream()
//                            .map(e -> e.getText() == null ? "" : e.getText().toString()) // 添加对 null 的检查
//                            .filter(Objects::nonNull)
//                            .collect(Collectors.joining()));
//                    if ("api".equals(source)) {
//                        if (StringUtils.isNotBlank(modelConfigVo.getModelRelocation())) {
//                            userOs.write(JSONObject.toJSONString(completionResult).getBytes(Charset.defaultCharset()));
//                        } else {
//                            userOs.write(aiResult.getBytes(Charset.defaultCharset()));
//                        }
//                        userOs.flush();
//                    }else{
//                        if(StringUtils.isNoneEmpty(chatMessage.getText().toString())){
//                            userOs.write(chatMessage.getText().toString().getBytes(Charset.defaultCharset()));
//                            userOs.flush();
//                        }
//                    }
//                    int relyTokenNumber = TokenUtil.countTokenText( Objects.nonNull(chatMessage.getText())?chatMessage.getText().toString():"", claudeCompletionRequest.getModel());
//
//                    log.info("claude no stream promptNumber={},aiPro={},relyTokenNumber={},aiRely={}",promptTokenNumber,completionResult.getUsage().getInput_tokens(),relyTokenNumber,completionResult.getUsage().getOutput_tokens());
//                    return ChatMessageResultVo.builder()
//                            .appId(modelConfigVo.getAppId())
//                            .chatMessage(null)
//                            .model(model)
//                            .promptTokenNumber((int)completionResult.getUsage().getInput_tokens())
//                            .relyTokenNumber((int)completionResult.getUsage().getOutput_tokens())
//                            .modelConfigId(modelConfigVo.getId())
//                            .userTokenId(userTokenVo.getId())
//                            .firstRelyTime(firstRTime.get())
//                            .costTime(System.currentTimeMillis() - currentTime)
//                            .build();
//                }
//                return null;
//            }
//            Reader reader = Objects.requireNonNull(responseBody).charStream();
//            BufferedReader bufferedReader = new BufferedReader(reader);
//            String line = null;
//
//            while ( (line=bufferedReader.readLine()) != null) {
//                try {
//                    ClaudeStreamCompletionChunk msg = convert(line);
//                    if(msg!=null){
//                        chunks.add(msg);
//                    }
//                    if(!first.get()){
//                        firstRTime.set(System.currentTimeMillis()-currentTime);
//                        first.set(true);
//                    }
//                    if (!flag.get()) {
//                        if ("api".equals(source)) {
//                            if (StringUtils.isNotBlank(modelConfigVo.getModelRelocation())) {
//                                userOs.write(("data: " + JSONObject.toJSONString(msg) + "\n\n").getBytes(Charset.defaultCharset()));
//                            } else {
//                                userOs.write((line + "\n\n").getBytes(Charset.defaultCharset()));
//                            }
//                            userOs.flush();
//                        } else if (msg!=null && msg.getDelta() != null) {
//                            ClaudeMessage choice = msg.getDelta();
//                            log.info("claude text={}", msg.getDelta().getText().toString());
//                            if (choice != null) {
//                                userOs.write(choice.getText().toString().getBytes(Charset.defaultCharset()));
//                                userOs.flush();
//                            }
//                        }
//                    }
//                }catch (MismatchedInputException e) {
//                    log.error("发生 MismatchedInputException 异常");
//                    flag.set(true);
//                } catch (ClientAbortException e) {
//                    if (!flag.get()) {
//                        log.error("api用户主动断开连接 userToken={} errorMsg={}", userTokenVo.getToken(),e.getMessage());
//                    }
//                    flag.set(true);
//                } catch (Exception e) {
//                    log.error("回答报错 token={},url={} chunks.size={},modelProcessorRequest={}",userTokenVo.getToken(), url, chunks.size(),chatRequestJson, e);
////                    throw new ModelErrorException("回答报错 token="+userTokenVo.getToken()+", url= "+url+",model="+model+",modelId="+modelConfigVo.getId()+"errorStack="+e);
//                }
//            }
//        }catch (CatchOpenAiHttpException e){
//            //发异常邮件
//            log.error("chatStream error token={} modelConfig={} url={},rCode={},modelProcessorRequest={}",userTokenVo.getToken(),modelConfigVo, url, e.statusCode,chatRequestJson,e);
//            String errorDetailStr = getErrorDetailStr(e, modelConfigVo, userTokenVo, model);
//            if (StringUtils.isNotBlank(errorDetailStr) && modelConfigVo.getCanAutoDisable() == 0){
//                jedisClient.rpush(RedisKeyUtil.MODEL_ERROR_KEY, errorDetailStr);
//            }
//            //抛日志,存入异常日志表
////            throw new ModelErrorException("请求异常 token="+userTokenVo.getToken()+" url="+url+",model="+model+",modelId="+modelConfigVo.getId()+",Rcode="+e.statusCode+"error log="+e);
////            ErrorLogUtils.errorLogSendEmail("ChatManager#chatStream",e.getMessage());
//        }catch (EOFException e) {
//            // 流已经结束
//            log.error("EOFException error  回答报错 url="+url+",model="+model+",modelId="+modelConfigVo.getId());
//        }catch (Exception e) {
//            log.error("chatStream error token={} modelId={} url={},modelProcessorRequest={}",userTokenVo.getToken(),modelConfigVo.getId(), url,chatRequestJson, e);
////            throw new ModelErrorException("回答报错 url="+url+",model="+model+",modelId="+modelConfigVo.getId()+"error log="+e);
//        }
//
//        ClaudeMessage chatMessage = new ClaudeMessage("text", chunks.stream()
//                .map(e -> e.getDelta() == null ? "" : e.getDelta().getText().toString()) // 添加对 null 的检查
//                .filter(Objects::nonNull)
//                .collect(Collectors.joining()));
//
//        int relyTokenNumber = TokenUtil.countTokenText(Objects.nonNull(chatMessage.getText())?chatMessage.getText().toString():"", model);
//        if( relyTokenNumber<=0 || promptTokenNumber<=0 ){
//            log.info("claude relyTokenNumber is 0 error  startAIModel   chunks={},modelConfigVo={}",JSONObject.toJSONString(chunks),JSONObject.toJSONString(modelConfigVo));
//        }
//
//        return ChatMessageResultVo.builder()
//                .appId(modelConfigVo.getAppId())
//                .chatMessage(null)
//                .model(model)
//                .promptTokenNumber(promptTokenNumber)
//                .relyTokenNumber(relyTokenNumber)
//                .modelConfigId(modelConfigVo.getId())
//                .userTokenId(userTokenVo.getId())
//                .firstRelyTime(firstRTime.get())
//                .costTime(System.currentTimeMillis() - currentTime)
//                .build();
//    }
//
//    public static ClaudeStreamCompletionChunk convert(String answer) {
//        if (StringUtils.isNoneEmpty(answer) && answer.contains("data: ") && answer.contains("content_block_delta")) {
//            String beanStr = answer.replaceFirst("data: ", "");
//            try {
//                return JSONObject.parseObject(beanStr, ClaudeStreamCompletionChunk.class);
//            }catch (Exception e){
//                log.error("claude convert error answer={}",answer,e);
//            }
//        }
//        return null;
//    }
//
//    private String checkIsRelocation(ClaudeCompletionRequest chatCompletionRequest,ModelConfigVo modelConfigVo){
//        if (StringUtils.isNotBlank(modelConfigVo.getModelRelocation())){
//            String modelRelocation = modelConfigVo.getModelRelocation();
//            try {
//                JSONObject.parseArray(modelRelocation, ModelRelocation.class).forEach(relocation -> {
//                            if (chatCompletionRequest.getModel().equals(relocation.getModel())){
//                                chatCompletionRequest.setModel(relocation.getModelRelocation());
//                            }
//                        }
//                );
//            } catch (Exception e) {
//                log.info("modelRelocationMap put error modelConfigVo={}", modelConfigVo, e);
//            }
//            log.info("checkIsRelocation modelConfigVo={} chatCompletionRequest={}",modelConfigVo,chatCompletionRequest);
//        }
//        return JSONObject.toJSONString(chatCompletionRequest);
//    }
//
//
//    @Override
//    public boolean match(String model, String template) {
//        return ModelTemplateEnum.CLAUDE_API.getTemplate().equals(template);
//    }
//
//}
