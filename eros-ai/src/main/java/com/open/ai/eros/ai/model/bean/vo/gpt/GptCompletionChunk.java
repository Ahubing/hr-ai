package com.open.ai.eros.ai.model.bean.vo.gpt;

import lombok.Data;

import java.util.List;

/**
 * {"id":"chatcmpl-8GpnXyZ2ZELHFqqABogRwivUoQDBZ",
 * "object":"chat.completion.chunk",
 * "created":1699023051,
 * "model":"gpt-3.5-turbo-0613",
 * "choices":
 * [
 *  {"index":0,
 *      "delta":{"content":"你"},
 *      "finish_reason":null}
 * ]
 *}
 *
 */
@Data
public class GptCompletionChunk {
    /**
     * A unique id assigned to this completion.
     */
    String id;

    /**https://beta.openai.com/docs/api-reference/create-completion
     * The type of object returned, should be "text_completion"
     */
    String object;

    /**
     * The creation time in epoch seconds.
     */
    long created;

    /**
     * The model used.
     */
    String model;

    /**
     * A list of generated completions.
     */
    List<GptCompletionChoice> choices;

    String system_fingerprint;
}
