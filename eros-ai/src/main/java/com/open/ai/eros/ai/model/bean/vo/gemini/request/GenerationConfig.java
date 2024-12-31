package com.open.ai.eros.ai.model.bean.vo.gemini.request;

import lombok.Data;

/**
 * @类名：GenerationConfig
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/17 16:44
 */

@Data
public class GenerationConfig {

    private int maxOutputTokens = 4096;

    private double temperature = 1.8;

}
