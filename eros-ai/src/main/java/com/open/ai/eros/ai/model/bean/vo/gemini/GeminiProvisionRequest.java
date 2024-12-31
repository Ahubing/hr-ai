package com.open.ai.eros.ai.model.bean.vo.gemini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeminiProvisionRequest {
    /**
     *   "model": "gemini-pro-vision",
     *   "prompt": "https://storage.googleapis.com/generativeai-downloads/images/scones.jpg 帮我识别这张图片, 从不同角度分析 给我结果"
     */
    private String model = "gemini-pro-vision";
    @NotEmpty(message = "prompt不能为空")
    private String prompt;
    private String size = "1024x1024";

    private String base64;
    private String source;
}
