package com.open.ai.eros.ai.model.bean.vo.gemini.request;

import lombok.Data;

/**
 * @类名：SafetySettings
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/17 14:57
 */
@Data
public class SafetySettings {


    private String category;

    private String threshold;


}
