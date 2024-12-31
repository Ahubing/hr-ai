package com.open.ai.eros.knowledge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @类名：PayConfig
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/27 20:35
 */
@Data
@ConfigurationProperties("knowledge.docs.ai")
@Configuration
public class KnowledgeAIConfig {

    /**
     * 代理地址
     */
    private String url;

    /**
     * 访问地址
     */
    private String token;

    /**
     * 问题推理模型
     */
    private String inferModel;

    /**
     * 问题检测模型
     */
    private String checkInferModel;

}
