package com.open.ai.eros.ai.lang.chain.provider.splitter;


import dev.langchain4j.data.document.DocumentSplitter;

public interface Splitter {

    String getName();

    DocumentSplitter getDocumentSplitter();

}
