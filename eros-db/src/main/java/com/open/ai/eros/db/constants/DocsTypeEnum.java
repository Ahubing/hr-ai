package com.open.ai.eros.db.constants;

public enum DocsTypeEnum {

    COMMON,//普通类  直接切割文档 然后开始向量
    KNOWLEDGE, // 知识类  切割文档  再将切片的内容 通过ai 进行内容反推 问题
    PLAN;// 访问类 直接将 标题进行 问题的变种



}
