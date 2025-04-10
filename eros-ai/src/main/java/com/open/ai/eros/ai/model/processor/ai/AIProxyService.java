package com.open.ai.eros.ai.model.processor.ai;

import com.open.ai.eros.ai.util.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @类名：AIProxyService
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/18 12:52
 */
@Slf4j
public class AIProxyService {


    private String baseUrl;

    /**
     * 第三方的token
     */
    public final String token;

    public OkHttpClient client;


    public AIProxyService(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.client = defaultClient();
    }


    public static OkHttpClient defaultClient() {
        try {
            return new OkHttpClient.Builder()
                    .sslSocketFactory(OkHttpUtil.getIgnoreInitedSslContext().getSocketFactory(), OkHttpUtil.IGNORE_SSL_TRUST_MANAGER_X509)
                    .hostnameVerifier(OkHttpUtil.getIgnoreSslHostnameVerifier())
                    .protocols( Collections.singletonList(Protocol.HTTP_1_1))
                    .connectionPool(new ConnectionPool(8000, 300, TimeUnit.SECONDS))
                    .readTimeout(600, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS)
                    .build();
        }catch (Exception e){
            log.error("defaultClient error",e);
            return new OkHttpClient.Builder()
//                    .addInterceptor(new ChatBaseAuthenticationInterceptor(urlConstants.getPrefix()+token,urlConstants.getAuthorization()))
                    .connectionPool(new ConnectionPool(8000, 300, TimeUnit.SECONDS))
                    .protocols( Collections.singletonList(Protocol.HTTP_1_1))
                    .readTimeout(600, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS)
                    .build();
        }
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public String getToken() {
        return token;
    }

    public OkHttpClient getClient() {
        return client;
    }
}
