package com.open.ai.eros.ai.lang.chain.test.parse;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;

/**
 * @类名：DocumentParser
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/10 11:33
 */
public class DocumentParser {


    public static void main(String[] args) {

        // Load a single document
        Document document = FileSystemDocumentLoader.loadDocument("C:\\Users\\陈臣\\Desktop\\q\\21nj1.DOC", new TextDocumentParser());

        //// Load all documents from a directory
        //List<Document> documents = FileSystemDocumentLoader.loadDocuments("C:\\Users\\陈臣\\Desktop\\1", new TextDocumentParser());
        //
        //// Load all *.txt documents from a directory
        //PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("*.html");
        //documents = FileSystemDocumentLoader.loadDocuments("C:\\Users\\陈臣\\Desktop\\1\\Kafka核心技术与实战-极客时间", pathMatcher, new TextDocumentParser());
        //
        //// Load all documents from a directory and its subdirectories
        //documents = FileSystemDocumentLoader.loadDocumentsRecursively("C:\\Users\\陈臣\\Desktop\\1\\Kafka核心技术与实战-极客时间", new TextDocumentParser());
        System.out.println(document.text());
        System.out.println();
    }


}
