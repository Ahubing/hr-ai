package com.open.ai.eros.ai.model.bean.vo.gemini.response;

import lombok.Data;

/**
 * @类名：UsageMetadata
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/17 15:03
 */

@Data
public class UsageMetadata {

    private int promptTokenCount;

    private int candidatesTokenCount;

    private int totalTokenCount;

}
