package com.open.ai.eros.ai.lang.chain.provider.embedding.bean;

import lombok.Data;

import java.util.List;

/**
 * An object containing a response from the answer api
 *
 * https://beta.openai.com/docs/api-reference/embeddings/create
 */
@Data
public class ChatBaseEmbeddingResult {

    /**
     * The GPTmodel used for generating embeddings
     */
    private String model;

    /**
     * The type of object returned, should be "list"
     */
    private String object;

    /**
     * A list of the calculated embeddings
     */
    private List<ChatBaseEmbedding> data;

    /**
     * The API usage for this request
     */
    private ChatBaseUsage usage;
}
