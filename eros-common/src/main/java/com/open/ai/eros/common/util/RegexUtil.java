package com.open.ai.eros.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {


    /**
     * 验证手机号
     *
     * @param hex
     * @return
     */
    public static boolean validatePhone(final String hex) {
        Pattern pattern;
        Matcher matcher;
        String PHONE_PATTERN = "^\\d{11}$";
        pattern = Pattern.compile(PHONE_PATTERN);
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    /**
     * 验证是否是versionModel的会话格式
     *
     * @param hex
     * @return
     */
    public static boolean validateVersionFormat(final String hex) {
        Pattern pattern;
        Matcher matcher;
        String PHONE_PATTERN = "^(http|https):\\/\\/[^\\s]+\\s.*$";
        pattern = Pattern.compile(PHONE_PATTERN);
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    /**
     * 验证手机号
     *
     * @param hex
     * @return
     */
    public static boolean validateEmail(final String hex) {
        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(hex);
        return matcher.matches();
    }


    public static void main(String[] args) {
        System.out.println(validateEmail("havejiuhao@gmail.com"));
        System.out.println(validatePhone("18230675983"));

        String content = "https://www.bluecatai123123.net/q.jpg 你好，帮我分析图片内容";
        boolean versionFormat = RegexUtil.validateVersionFormat(content);
        if (versionFormat) {
            String[] contents = content.split("\\s", 2);
            String url = contents[0];
            String prompt = contents[1];
            ////			// 创建文本内容的JSON对象
            JSONArray jsonArray = new JSONArray();
            JSONObject textObject = new JSONObject();
            textObject.put("type", "text");
            textObject.put("text", prompt);

            // 创建图片URL的JSON对象
            JSONObject imageObject = new JSONObject();
            imageObject.put("type", "image_url");

            JSONObject urlObject = new JSONObject();
            urlObject.put("url", url);
            imageObject.put("image_url", urlObject);

            // 将JSON对象添加到JSON数组
            jsonArray.add(textObject);
            jsonArray.add(imageObject);
            // 输出JSON字符串
            System.out.println(jsonArray);
        }
    }
}
