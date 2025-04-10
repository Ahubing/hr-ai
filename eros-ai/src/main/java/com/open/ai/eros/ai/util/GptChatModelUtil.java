package com.open.ai.eros.ai.util;

/**
 * @类名：EmbdingModelUtil
 * @项目名：blue-cat-api
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/3/27 22:32
 */

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.model.bean.vo.gpt.ChatCompletionResult;
import com.open.ai.eros.ai.model.bean.vo.gpt.GptCompletionRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 调用 gpt会话 模型的入口
 *
 */

@Slf4j
public class GptChatModelUtil {

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(500, 10,TimeUnit.SECONDS))
            .readTimeout(100, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
                        .writeTimeout(30,TimeUnit.SECONDS)  // 写入超时
                        .build();

    private static String getUrl(String cdnHost){
        return cdnHost.endsWith("/")?cdnHost+"v1/chat/completions":cdnHost+"/v1/chat/completions";
    }

    /**
     * gpt的非流式对话
     *
     * @param chatCompletionRequest
     * @param token
     * @param baseUrl
     * @return
     */
    public static ChatCompletionResult startChatWithNoStream(GptCompletionRequest chatCompletionRequest, String token, String baseUrl){
        try {
            chatCompletionRequest.setStream(false);
            String chatRequestJson = JSONObject.toJSONString(chatCompletionRequest);
            String url = getUrl(baseUrl);
            RequestBody body =  RequestBody.create(MediaType.parse("application/json; charset=utf-8"),chatRequestJson );
            Request.Builder builder = new Request.Builder().url(url).method("POST", body)
                    .addHeader("Authorization", "Bearer " + token);

            Request request = builder.build();
            Response execute = client.newCall(request).execute();
            ResponseBody responseBody = execute.body();
            //非流式的
            String aiResult = responseBody.string();
            if(StringUtils.isNoneEmpty(aiResult)) {
                return JSONObject.parseObject(aiResult, ChatCompletionResult.class);
            }
        }catch (Exception e){
            log.error("startChatWithNoStream error url={}",baseUrl,e);
        }
        return null;
    }

}
