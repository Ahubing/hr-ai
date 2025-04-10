package com.open.ai.eros.knowledge.service;

/**
 * @类名：VectorTextService
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/15 0:04
 */
public class VectorTextService {


    public static String simpleHandleText(String text){
        return text.replace(" ","").replace("\n","");
    }

}
