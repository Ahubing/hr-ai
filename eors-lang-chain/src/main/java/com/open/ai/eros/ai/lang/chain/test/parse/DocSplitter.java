package com.open.ai.eros.ai.lang.chain.test.parse;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.document.transformer.jsoup.HtmlToTextDocumentTransformer;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiTokenizer;

import java.util.List;

/**
 * @类名：DocSplitter
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/10 11:56
 */
public class DocSplitter {

    public static void main(String[] args) {

        Document document = FileSystemDocumentLoader.loadDocument("C:\\Users\\陈臣\\Desktop\\q\\21nj1.DOC", new TextDocumentParser());
        HtmlToTextDocumentTransformer htmlToTextDocumentTransformer = new HtmlToTextDocumentTransformer();
        String text = htmlToTextDocumentTransformer.transform(document).text();

        DocumentByParagraphSplitter documentByParagraphSplitter = new DocumentByParagraphSplitter(100,100);
        String[] split = documentByParagraphSplitter.split(text);
        //for (String string : split) {
        //    System.out.println(string);
        //    System.out.println("------------------------------------------");
        //}

        System.out.println("*******************************************************************");
        DocumentByWordSplitter documentByParagraphSplitter1 = new DocumentByWordSplitter(100, 100);

        //String[] split1 = documentByParagraphSplitter1.split(text);
        //for (String string : split1) {
        //    System.out.println(string);
        //    System.out.println("------------------------------------------");
        //}

        DocumentSplitter recursive = DocumentSplitters.recursive(1000, 200, new OpenAiTokenizer());
        List<TextSegment> textSegments = recursive.split(htmlToTextDocumentTransformer.transform(document));
        for (TextSegment textSegment : textSegments) {
            System.out.println(textSegment.text());
            System.out.println("------------------------------------------");
        }
    }


}
