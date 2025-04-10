package com.open.ai.eros.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @类名：AIJsonUtil
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2025/1/17 21:36
 */
public class AIJsonUtil {


    public static String getJsonContent(String aiCode) {
        if (StringUtils.isEmpty(aiCode)) {
            return null;
        }
        String preFix = "```json";
        String endFix = "```";
        int startIndex = aiCode.indexOf(preFix);
        if (startIndex == -1) {
            return aiCode;
        }
        startIndex += preFix.length();
        int indexed = aiCode.lastIndexOf(endFix);
        return aiCode.substring(startIndex, indexed);
    }

    public static void main(String[] args) {
        String aiCode = "{\n" +
                "  \"messages\": [\n" +
                "    \"你的简历符合我们的招聘要求，可以安排面试。\",\n" +
                "    \"这周可选面试时间：4月4日15:00、4月5日10:30、4月7日14:00，哪个时间方便？\"\n" +
                "  ]\n" +
                "}\n" ;
        String jsonContent = getJsonContent(aiCode);
        System.out.println(jsonContent);
        JSONObject jsonObject = JSONObject.parseObject(jsonContent);
        Object data = jsonObject.get("experiences");
        System.out.println(data.toString());

    }

}
