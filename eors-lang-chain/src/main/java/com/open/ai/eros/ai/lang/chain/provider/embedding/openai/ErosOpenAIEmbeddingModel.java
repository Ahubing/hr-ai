package com.open.ai.eros.ai.lang.chain.provider.embedding.openai;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.lang.chain.provider.embedding.bean.ChatBaseEmbeddingRequest;
import com.open.ai.eros.ai.lang.chain.provider.embedding.bean.ChatBaseEmbeddingResult;
import com.open.ai.eros.ai.lang.chain.provider.embedding.bean.ChatBaseUsage;
import com.open.ai.eros.common.exception.BizException;
import dev.ai4j.openai4j.shared.Usage;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.internal.ValidationUtils;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.openai.InternalOpenAiHelper;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.net.Proxy;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
   * @类名：OpenAIEmbeddingModel
   * @项目名：web-eros-ai
   * @description：
   * @创建人：陈臣
   * @创建时间：2024/9/12 2:52
   */
@Slf4j
public class ErosOpenAIEmbeddingModel extends OpenAiEmbeddingModel {


    static final String OPENAI_URL = "https://api.openai.com/v1";

    private OkHttpClient client;

    private String baseUrl;

    private String apiKey;

    private String modelName;


    public ErosOpenAIEmbeddingModel(String baseUrl, String apiKey,String modelName, Integer dimensions, String user,  Boolean logRequests, Boolean logResponses) {
        super(baseUrl, apiKey, null, modelName, dimensions, user, null, null, null, logRequests, logResponses, null, null);
        this.client = new OkHttpClient.Builder()
                .protocols( Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(8000, 300, TimeUnit.SECONDS))
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        this.apiKey = apiKey;
        this.baseUrl =  Utils.getOrDefault(baseUrl, OPENAI_URL);
        this.modelName = modelName;
    }



    public ErosOpenAIEmbeddingModel(String baseUrl, String apiKey, String organizationId, String modelName, Integer dimensions, String user, Duration timeout, Integer maxRetries, Proxy proxy, Boolean logRequests, Boolean logResponses, Tokenizer tokenizer, Map<String, String> customHeaders) {
        super(baseUrl, apiKey, organizationId, modelName, dimensions, user, timeout, maxRetries, proxy, logRequests, logResponses, tokenizer, customHeaders);
        this.client = new OkHttpClient.Builder()
                .protocols( Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(8000, 300, TimeUnit.SECONDS))
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        this.apiKey = apiKey;
        this.baseUrl =  Utils.getOrDefault(baseUrl, OPENAI_URL);
    }

    @Override
    public Response<Embedding> embed(String text) {
        return this.embed(TextSegment.from(text));
    }

    private String getUrl(String cdnHost){
        return cdnHost.endsWith("/")?cdnHost+"v1/embeddings":cdnHost+"/v1/embeddings";
    }


    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        long currentTimeMillis = System.currentTimeMillis();
        List<String> texts = textSegments.stream()
                .map(TextSegment::text)
                .collect(toList());

        ChatBaseEmbeddingRequest request = ChatBaseEmbeddingRequest.builder()
                .input(texts)
                .model(modelName)
                .dimensions(super.dimension())
                .build();

        String url = getUrl(this.baseUrl);

        RequestBody body =  RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSONObject.toJSONString(request));
        Request.Builder builder = new Request.Builder().url(url).method("POST", body)
                .addHeader("Authorization", "Bearer " + this.apiKey);

        Request httpRequest = builder.build();
        try {
            okhttp3.Response execute = client.newCall(httpRequest).execute();
            ResponseBody responseBody = execute.body();
            //非流式的
            String aiResult = responseBody.string();

           if(StringUtils.isNoneEmpty(aiResult)){
               ChatBaseEmbeddingResult response = JSONObject.parseObject(aiResult, ChatBaseEmbeddingResult.class);

               List<Embedding> embeddings = response.getData().stream()
                       .map(openAiEmbedding -> Embedding.from(openAiEmbedding.getEmbedding()))
                       .collect(toList());

               ChatBaseUsage usage = response.getUsage();

               Usage usage2 = Usage.builder().completionTokens(usage.getCompletion_tokens())
                       .promptTokens(usage.getPrompt_tokens())
                       .totalTokens(usage.getTotal_tokens()).build();

               return Response.from(
                       embeddings,
                       InternalOpenAiHelper.tokenUsageFrom(usage2));
           }
        }catch (Exception e){
            log.error("embedAll error",e);
            throw new BizException(e.getMessage());
        }finally {
            log.info("open ai embedding costTime={}",System.currentTimeMillis()-currentTimeMillis);
        }
        return null;
    }


    @Override
    public Response<Embedding> embed(TextSegment textSegment) {
        Response<List<Embedding>> response = this.embedAll(singletonList(textSegment));
        ValidationUtils.ensureEq(response.content().size(), 1,
                "Expected a single embedding, but got %d", response.content().size());
        return Response.from(response.content().get(0), response.tokenUsage(), response.finishReason());
    }





}
