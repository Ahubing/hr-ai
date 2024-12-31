package com.open.ai.eros.ai.lang.chain.provider.splitter;

import com.open.ai.eros.ai.lang.chain.provider.build.ModelVectorEnum;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @类名：CommonDocsSplitter
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/29 13:04
 */

@Component
@Data
public class CommonDocsSplitter implements Splitter {

    private String name = "通用的段落文章切割";

    private DocumentByRegexSplitter documentSplitter =  new DocumentByRegexSplitter("\\s([一二三四五六七八九十]+、|\\d+\\.)+","",200,0,new OpenAiTokenizer(ModelVectorEnum.text_embedding_small_3.getEncodingForModel()));

}
