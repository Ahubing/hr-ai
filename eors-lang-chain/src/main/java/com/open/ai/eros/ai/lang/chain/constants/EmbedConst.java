package com.open.ai.eros.ai.lang.chain.constants;

/**
 * @类名：EmbedConst
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/11 19:37
 */
public interface EmbedConst {


    String ORIGIN_TYPE_INPUT = "INPUT";
    String ORIGIN_TYPE_UPLOAD = "UPLOAD";

    String KNOWLEDGE = "knowledgeId";

    String SPLICE_ID = "sliceId";

    String FILENAME = "docsName";

    /**
     * 各种模型的向量化的标识
     *
     */
    String CLAZZ_NAME_OPENAI = "OpenAiEmbeddingModel";
    String CLAZZ_NAME_AZURE_OPENAI = "AzureOpenAiEmbeddingModel";
    String CLAZZ_NAME_QIANFAN = "QianfanEmbeddingModel";
    String CLAZZ_NAME_QIANWEN = "QwenEmbeddingModel";
    String CLAZZ_NAME_ZHIPU = "ZhipuAiEmbeddingModel";
    String CLAZZ_NAME_OLLAMA = "OllamaEmbeddingModel";

}
