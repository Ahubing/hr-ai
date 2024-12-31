package com.open.ai.eros.pay.util;

/**
 * @类名：ChPayUtil
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/4 13:38
 */

import com.open.ai.eros.common.util.ObjectToHashMapConverter;
import com.open.ai.eros.pay.order.bean.dto.GetPayUrlDto;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 彩虹支付的工具类
 */
public class ChPayUtil {



    public static String createLinkString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        // 删除最后一个&符号
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


    /**
     * 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串，并对字符串做URL编码
     * @param params 需要拼接的Map
     * @return 拼接完成以后的字符串
     */
    public static String createLinkStringUrlEncode(Map<String, String> params) {
        Set<Map.Entry<String, String>> entries = params.entrySet();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : entries) {
            try {
                sb.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


    /**
     * 对Map按键排序
     * @param map 需要排序的Map
     * @return 排序后的Map
     */
    public static Map<String, String> argSort(Map<String, String> map) {
        Map<String, String> sortedMap = new TreeMap<>(map);
        return Collections.unmodifiableMap(sortedMap);
    }


    //public static String md5Sign(String preStr, String key) {
    //    try {
    //        preStr += key;
    //        MessageDigest md = MessageDigest.getInstance("MD5");
    //        byte[] bytes = md.digest(preStr.getBytes("UTF-8"));
    //        StringBuilder sb = new StringBuilder();
    //        for (byte b : bytes) {
    //            String hex = Integer.toHexString(0xff & b);
    //            if (hex.length() == 1) sb.append('0');
    //            sb.append(hex);
    //        }
    //        return sb.toString().toLowerCase();
    //    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
    //        throw new RuntimeException("Error in MD5 signing: " + e.getMessage());
    //    }
    //}


    public static void main(String[] args) {
        GetPayUrlDto dto = new GetPayUrlDto();
        dto.setName("测试");
        dto.setType("alipay");
        dto.setClientip("127.0.0.1");
        dto.setNotify_url("http://127.0.0.1:8099");
        Map<String, String> stringStringMap = ObjectToHashMapConverter.convertObjectToHashMap(dto);
        System.out.println(stringStringMap);
        Map<String, String> stringStringMap1 = argSort(stringStringMap);
        System.out.println(stringStringMap1);
        String linkString = createLinkString(stringStringMap1);
        System.out.println(linkString);
        //System.out.println(md5Sign(linkString,"123"));
    }



}
