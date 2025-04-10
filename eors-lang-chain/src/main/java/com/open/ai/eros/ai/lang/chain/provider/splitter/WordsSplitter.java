package com.open.ai.eros.ai.lang.chain.provider.splitter;

import com.open.ai.eros.ai.lang.chain.provider.build.ModelVectorEnum;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiTokenizer;

import java.util.List;

/**
 * @类名：WordsSplitter
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/15 13:16
 */
public class WordsSplitter implements DocumentSplitter {

    public static void main(String[] args) {
        String content = "";
        ModelVectorEnum modelVectorEnum = ModelVectorEnum.text_embedding_large_3;
        DocumentByWordSplitter recursive = new DocumentByWordSplitter(600, 0, new OpenAiTokenizer(modelVectorEnum.getEncodingForModel()));
        //CommonSplitter recursive = DocumentSplitters.recursive(600, 0, new OpenAiTokenizer(modelVectorEnum.getEncodingForModel()));
        Document document  = new Document(content);
        List<TextSegment> textSegments = recursive.split(document);
        for (TextSegment textSegment : textSegments) {
            String text = textSegment.text();
            System.out.println();
            System.out.println(text);
        }
    }

    @Override
    public List<TextSegment> split(Document document) {
        return null;
    }

    @Override
    public List<TextSegment> splitAll(List<Document> documents) {
        return DocumentSplitter.super.splitAll(documents);
    }
}
