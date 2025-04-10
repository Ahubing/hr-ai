package com.open.ai.eros.ai.model.bean.vo.gemini;

import lombok.Data;

import java.util.LinkedList;

/**
 * @类名：GeminiMessage
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/17 14:42
 */
@Data
public class GeminiMessage {

    private String role;

    private LinkedList<GeminiParts> parts;

}
