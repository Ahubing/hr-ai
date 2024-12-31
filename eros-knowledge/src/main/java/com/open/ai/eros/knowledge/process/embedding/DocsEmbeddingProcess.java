package com.open.ai.eros.knowledge.process.embedding;

import com.open.ai.eros.db.mysql.knowledge.entity.Knowledge;
import com.open.ai.eros.db.mysql.knowledge.entity.KnowledgeDocs;

public interface DocsEmbeddingProcess {

    /**
     * 向量化
     *
     * @param knowledge
     * @param knowledgeDocs
     */
    void embedding(Knowledge knowledge, KnowledgeDocs knowledgeDocs,String name);


    boolean match(String type);


}
