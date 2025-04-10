package com.open.ai.eros.ai.lang.chain.provider.embedding.bean;

import lombok.Data;

import java.util.List;

/**
 * Represents an embedding returned by the embedding api
 *
 * https://beta.openai.com/docs/api-reference/classifications/create
 */
@Data
public class ChatBaseEmbedding {

    /**
     * The type of object returned, should be "embedding"
     */
    private String object;

    /**
     * The embedding vector
     */
    private List<Float> embedding;

    /**
     * The position of this embedding in the list
     */
    private Integer index;
}
