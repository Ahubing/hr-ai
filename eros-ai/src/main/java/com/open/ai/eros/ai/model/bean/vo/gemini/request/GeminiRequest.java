package com.open.ai.eros.ai.model.bean.vo.gemini.request;

import com.open.ai.eros.ai.model.bean.vo.gemini.GeminiMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

/**
 * @类名：GeminiRequest
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/17 14:41
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GeminiRequest {

    private String model;

    private SystemInstructionParts systemInstruction;

    private LinkedList<GeminiMessage> contents;

    private LinkedList<SafetySettings> safetySettings;

    private GenerationConfig generationConfig;

}
