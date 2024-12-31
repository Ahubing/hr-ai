package com.open.ai.eros.ai.model.processor.ai;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.constatns.ModelTemplateEnum;
import com.open.ai.eros.ai.model.bean.vo.ChatMessageResultVo;
import com.open.ai.eros.ai.model.bean.vo.ModelProcessorRequest;
import com.open.ai.eros.ai.model.bean.vo.gemini.GeminiMessage;
import com.open.ai.eros.ai.model.bean.vo.gemini.GeminiParts;
import com.open.ai.eros.ai.model.bean.vo.gemini.request.GeminiRequest;
import com.open.ai.eros.ai.model.bean.vo.gemini.response.CandidatesMessage;
import com.open.ai.eros.ai.model.bean.vo.gemini.response.GeminiCandidates;
import com.open.ai.eros.ai.model.bean.vo.gemini.response.UsageMetadata;
import com.open.ai.eros.ai.model.processor.BaseModelProcessor;
import com.open.ai.eros.ai.model.processor.ChatModelProcessor;
import com.open.ai.eros.ai.util.SendMessageUtil;
import com.open.ai.eros.ai.util.TokenUtil;
import com.open.ai.eros.common.constants.BaseCodeEnum;
import com.open.ai.eros.common.exception.BizException;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @类名：GptChatProcossor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/3 15:01
 */

@Component
@Slf4j
public class GeminiChatProcessor extends BaseModelProcessor implements ChatModelProcessor {


    @Override
    public ChatMessageResultVo startAIModel(ModelProcessorRequest modelProcessorRequest, SendMessageUtil sendMessageUtil, ModelConfigVo modelConfigVo) throws IOException {
        String token= modelConfigVo.getToken();
        String model = modelProcessorRequest.getModel();
        GeminiRequest geminiRequest = JSONObject.parseObject(modelProcessorRequest.getRequest(), GeminiRequest.class);
        geminiRequest.setModel(model);

        String url;
        if(getUrl(modelConfigVo.getBaseUrl()).contains("%s")){
            url= String.format(getUrl(modelConfigVo.getBaseUrl()),model,modelConfigVo.getToken());
        }else {
            url = getUrl(modelConfigVo.getBaseUrl());
        }
        //AITextChatVo chatVo = modelProcessorRequest.getChatVo();
        //sendMessageUtil.sendMessage(String.format(SendMessageUtil.CHAT_ID,chatVo.getChatId()));

        UsageMetadata usageMetadata = null;
        try {
            AIProxyService proxyService = AIProxyServiceFactory.getProxyService(modelConfigVo);
            if (proxyService == null) {
                sendMessageUtil.sendMessage(BaseCodeEnum.NO_MODEL.getMsg());
                throw new BizException("获取连接失败！");
            }
            OkHttpClient okHttpClient = proxyService.getClient();
            log.info("JSONObject.toJSONString(geminiRequest) = {}",JSONObject.toJSONString(geminiRequest));
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), JSONObject.toJSONString(geminiRequest));
            Request request = new Request.Builder().url(url).method("POST", body)
                    .addHeader("Authorization", "Bearer "+token)
                    .build();

            Response execute = okHttpClient.newCall(request).execute();
            super.checkException(execute);
            ResponseBody responseBody = execute.body();
            Reader reader = Objects.requireNonNull(responseBody).charStream();
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = null;
            List<GeminiParts> chunks = new ArrayList<>();

            long currentTime = System.currentTimeMillis();
            AtomicReference<Long> firstRTime = new AtomicReference<>();
            AtomicBoolean flag = new AtomicBoolean(false);
            AtomicBoolean first = new AtomicBoolean(false);
            // 流式输出
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    GeminiCandidates msg = convert(line);
                    if (!first.get()) {
                        firstRTime.set(System.currentTimeMillis() - currentTime);
                        first.set(true);
                    }
                    if (!flag.get() && line.startsWith("data:")) {

                        if(msg!=null && msg.getUsageMetadata()!=null){
                            usageMetadata = msg.getUsageMetadata();
                        }

                        if (msg != null && CollectionUtils.isNotEmpty(msg.getCandidates())) {
                            CandidatesMessage choice = msg.getCandidates().get(0);
                            if (choice.getContent() != null && CollectionUtils.isNotEmpty(choice.getContent().getParts())) {
                                GeminiParts part = choice.getContent().getParts().get(0);
                                chunks.add(part);
                                sendMessageUtil.sendMessage(sendMessageUtil.formatAIStr(part.getText(),false));
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("回答报错 token={},url={} chunks.size={},modelProcessorRequest={}", token,url, chunks.size(), JSONObject.toJSONString(geminiRequest), e);
                }
            }

            ChatMessage chatMessage = new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), chunks.stream()
                    .map(GeminiParts::getText)
                    .map(e -> e == null ? "" : e) // 添加对 null 的检查
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining())
                    );

            int promptTokenNumber = 0;
            int relyTokenNumber = 0;
            if(usageMetadata==null){
               LinkedList<GeminiMessage> contents = geminiRequest.getContents();
               String userPrompt = contents.stream().map(GeminiMessage::getParts).map(e -> e.get(0)).map(e -> e == null ? "" : e.getText()).collect(Collectors.joining());
               promptTokenNumber = TokenUtil.countTokenText(userPrompt, model);
               relyTokenNumber = TokenUtil.countTokenText(Objects.nonNull(chatMessage.getContent()) ? chatMessage.getContent().toString() : "", model);
            }else{
                promptTokenNumber = usageMetadata.getPromptTokenCount();
                relyTokenNumber = usageMetadata.getCandidatesTokenCount();
            }

            //Long aiChatMessageId = chatVo.getAiChatMessageId();
            //if(aiChatMessageId!=null){
            //    sendMessageUtil.sendMessage(String.format(SendMessageUtil.REPLY_ID,aiChatMessageId));
            //}

            return ChatMessageResultVo.builder()
                    .chatMessage(chatMessage)
                    .model(model)
                    .promptTokenNumber(promptTokenNumber)
                    .relyTokenNumber(relyTokenNumber)
                    .firstRelyTime(firstRTime.get())
                    .costTime(System.currentTimeMillis() - currentTime)
                    .build();

        } catch (Exception e) {
            log.error("gemini Chat error messages={} ", JSONObject.toJSONString(geminiRequest),e);
        }
        return null;
    }

    @Override
    public boolean match(String model, String template) {
        return ModelTemplateEnum.GEMINI_API.getTemplate().equals(template);
    }

    @Override
    public String getUrl(String cdnHost) {
        if(!cdnHost.contains("generativelanguage.googleapis.com")){
            return cdnHost.endsWith("/") ? cdnHost + "v1beta/models" : cdnHost + "/v1beta/models";
        }
        //https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:streamGenerateContent?alt=sse&key=AIzaSyAvKPHhYRRakRZqlcbQmMODCYlKay_y4mI
        // /v1beta/models/gemini-1.5-flash:streamGenerateContent?alt=sse&key=AIzaSyAvKPHhYRRakRZqlcbQmMODCYlKay_y4mI
        return cdnHost.endsWith("/") ? cdnHost + "v1beta/models/%s:streamGenerateContent?alt=sse&key=%s" : cdnHost + "/v1beta/models/%s:streamGenerateContent?alt=sse&key=%s";
    }




    public static GeminiCandidates convert(String answer) {
        if (StringUtils.isNoneEmpty(answer) && !"data: [DONE]".equals(answer)) {
            String beanStr = answer.replaceFirst("data: ", "");
            try {
                return JSONObject.parseObject(beanStr, GeminiCandidates.class);
            } catch (Exception e) {
                log.error("convert error answer={}", answer, e);
            }
        }
        return null;
    }


}
