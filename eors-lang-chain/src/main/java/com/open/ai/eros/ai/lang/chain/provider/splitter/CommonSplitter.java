package com.open.ai.eros.ai.lang.chain.provider.splitter;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @类名：DocumentSplitter
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/29 16:26
 */
@Data
@Component
public class CommonSplitter implements Splitter{


    private String name = "常用的切割200字";

    private DocumentSplitter documentSplitter =  DocumentSplitters.recursive(200, 0);
}
