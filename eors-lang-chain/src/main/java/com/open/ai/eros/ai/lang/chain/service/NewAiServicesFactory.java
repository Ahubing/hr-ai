package com.open.ai.eros.ai.lang.chain.service;

import dev.langchain4j.service.AiServiceContext;

/**
 * @类名：AiServicesFactory
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/14 23:52
 */
public interface NewAiServicesFactory {

    <T> NewAiServices<T> create(AiServiceContext var1);


}
