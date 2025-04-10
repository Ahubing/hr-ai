package com.open.ai.eros.ai.model.bean.vo.gemini.response;

import com.open.ai.eros.ai.model.bean.vo.gemini.GeminiMessage;
import lombok.Data;

/**
 * @类名：CandidatesMessage
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/17 15:00
 */
@Data
public class CandidatesMessage {

    private GeminiMessage content;

    private String finishReason;

    private Integer index;


}
