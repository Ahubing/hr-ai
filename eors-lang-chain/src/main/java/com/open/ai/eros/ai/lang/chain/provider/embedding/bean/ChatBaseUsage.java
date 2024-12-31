package com.open.ai.eros.ai.lang.chain.provider.embedding.bean;

import lombok.Data;

/**
 * The OpenAI resources used by a request
 */
@Data
public class ChatBaseUsage {
    /**
     * The number of prompt tokens used.
     */
    private Integer prompt_tokens;

    /**
     * The number of completion tokens used.
     */
    private Integer completion_tokens;

    /**
     * The number of total tokens used
     */
    private Integer total_tokens;
}
