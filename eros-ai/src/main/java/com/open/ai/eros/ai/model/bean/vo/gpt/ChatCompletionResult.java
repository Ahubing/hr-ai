package com.open.ai.eros.ai.model.bean.vo.gpt;

import com.open.ai.eros.ai.lang.chain.provider.embedding.bean.ChatBaseUsage;
import lombok.Data;

import java.util.List;

/**
 * An object containing a response from the completion api
 *
 * https://beta.openai.com/docs/api-reference/completions/create
 */
@Data
public class ChatCompletionResult {
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
     * The GPT model used.
     */
    String model;

    /**
     * A list of generated completions.
     */
    List<CompletionMessageChoice> choices;

    /**
     * The API usage for this request
     */
    ChatBaseUsage usage;

    String  system_fingerprint;


}
