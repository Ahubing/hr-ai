package com.open.hr.ai.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @类名：VariableUtil
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/12/22 11:06
 */
public class VariableUtil {


    public static void main(String[] args) {
        List<String> strings = regexVariable("这是标签<h1>[标题]{jiuhao}</h1>");
        System.out.println(strings);

        String html = "[图片:</span><span style=\"font-family: &quot;Microsoft YaHei&quot;; color: rgb(0, 0, 0); font-size: 16px;\">概述1</span><span style=\"font-family: &quot;Microsoft YaHei&quot;; color: rgb(0, 0, 0); font-size: 14px;\">:276:254]";
        String regex = "\\[图片:</span><span[^>]+>概述\\d+(</span><span[^>]+>)?:\\d+:\\d+\\]";
        System.out.println(html.replaceAll(regex,""));
        //html = extractText(html);
        //html = html.replaceAll("&nbsp;","");
        //List<String> strings1 = regexHtmlVariable(html);
        //System.out.println(strings1);

    }

    /**
     * 提取 HTML 中的文本内容
     *
     * @param html HTML 内容
     * @return 文本内容
     */
    public static String extractHtmlText(String html) {
        // 正则表达式匹配 HTML 标签
        String regex = "<[^>]+>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        // 替换所有 HTML 标签为空字符串
        return matcher.replaceAll("");
    }


    public static List<String> regexHtmlVariable(String html){
        html = extractHtmlText(html);
        html = html.replaceAll("&nbsp;","");
        // 正则表达式匹配大括号中的内容
        String regex = "\\[(.*?)\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        List<String> results = new ArrayList<>();
        // 查找所有匹配项
        while (matcher.find()) {
            // 获取大括号中的内容
            String content = matcher.group(1);
            results.add(content);
        }
        return results;
    }

    public static List<String> regexVariable(String variable){
        // 正则表达式匹配大括号中的内容
        // 定义正则表达式
        String regex = "\\{(.*?)\\}";

        // 创建Pattern对象
        Pattern pattern = Pattern.compile(regex);
        // 创建Matcher对象
        Matcher matcher = pattern.matcher(variable);
        List<String> results = new ArrayList<>();

        // 查找所有匹配项
        while (matcher.find()) {
            // 获取大括号中的内容
            String content = matcher.group(1);
            results.add(content);
        }
        return results;
    }


}
