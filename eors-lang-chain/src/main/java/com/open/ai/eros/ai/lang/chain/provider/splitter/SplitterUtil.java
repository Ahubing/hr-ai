package com.open.ai.eros.ai.lang.chain.provider.splitter;

import dev.langchain4j.data.document.DocumentSplitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @类名：SplitterUtil
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/29 16:19
 */
@Component
public class SplitterUtil{


    @Autowired
    private List<Splitter> splitterList;

    private static Map<String, DocumentSplitter> splitterMap = new ConcurrentHashMap<>();

    private static Splitter commonSplitter = new CommonSplitter();


    @PostConstruct
    public void initSplitter(){
        for (Splitter splitter : splitterList) {
            String name = splitter.getClass().getName();
            splitterMap.put(name,splitter.getDocumentSplitter());
        }
    }


    public static void main(String[] args) {
        System.out.println(commonSplitter.getClass().getSimpleName());
    }


    /**
     * 获取此切割器
     *
     * @param name
     * @return
     */
    public static DocumentSplitter getSplitter(String name){
        if(StringUtils.isEmpty(name)){
            return commonSplitter.getDocumentSplitter();
        }
        return splitterMap.getOrDefault(name,commonSplitter.getDocumentSplitter());
    }

}
