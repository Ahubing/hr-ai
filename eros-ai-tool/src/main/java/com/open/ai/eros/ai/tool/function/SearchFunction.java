package com.open.ai.eros.ai.tool.function;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.web.search.WebSearchTool;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @类名：SearchFunction
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/24 19:42
 */
@Component
public class SearchFunction {


    @Tool(name = "baidu_search",value = "百度网页搜索")
    public String baiduSearch(@P("百度搜索关键字") String query){

        SearchApiWebSearchEngine searchEngine = SearchApiWebSearchEngine.builder()
                .apiKey("44ZC9Z6Yj17A4M2RVzUPpKDo")
                .engine("baidu")
                .build();
        WebSearchTool webTool = WebSearchTool.from(searchEngine);
        return webTool.searchWeb(query);
    }


    @Tool(name = "google_search",value = "谷歌网页搜索")
    public String googleSearch(@P("谷歌搜索关键字") String query){
        Map<String, Object> optionalParameters = new HashMap<>();
        optionalParameters.put("gl", "hk");
        optionalParameters.put("hl", "en");
        optionalParameters.put("google_domain", "google.com.hk");

        SearchApiWebSearchEngine searchEngine = SearchApiWebSearchEngine.builder()
                .apiKey("44ZC9Z6Yj17A4M2RVzUPpKDo")
                .engine("google")
                .optionalParameters(optionalParameters)
                .build();
        WebSearchTool webTool = WebSearchTool.from(searchEngine);
        return webTool.searchWeb(query);
    }


}
