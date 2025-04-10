package com.open.ai.eros.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.ai.entity.ModelConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @类名：ModelConfigUtil
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/25 21:56
 */
@Slf4j
public class ModelConfigUtil {

    /**
     * 通过授权码获取 渠道
     * @return
     */
    public static List<ModelConfig> getModelConfig(){
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        String authorizationCode = System.getProperty("authorizationCode");
        String url = System.getProperty("modelUrl");
        if(StringUtils.isEmpty(authorizationCode) || StringUtils.isEmpty(url) ){
            return Collections.EMPTY_LIST;
        }
        Request.Builder builder = new Request.Builder().url(url)
                .addHeader("Authorization", "Bearer " + authorizationCode).get();
        Request httpRequest = builder.build();
        try {
            okhttp3.Response execute = client.newCall(httpRequest).execute();
            ResponseBody responseBody = execute.body();
            //非流式的
            assert responseBody != null;
            String aiResult = responseBody.string();
            log.info("getModelConfig aiResult={}",aiResult);
            return JSONObject.parseArray(aiResult,ModelConfig.class);
        }catch (Exception e){
            log.error("getModelConfig url={} authorizationCode={}",url,authorizationCode,e);
        }
        return Collections.EMPTY_LIST;
    }
}
