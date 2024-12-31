package com.open.ai.eros.creator.bean.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @类名：loraVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/30 14:46
 */

/**
 * [{\"content\":\"彩蛋\",\"keyword\":[\"村上春树\"],\"title\":\"村上春树\"}]
 */
@Data
public class loraVo {

    private String content;

    private List<String> keyword;

    private String title;


    public static void main(String[] args) {
        List<loraVo> loraVos = new ArrayList<>();
        loraVo loraVo = new loraVo();
        loraVo.setContent("彩蛋");
        loraVo.setTitle("村上春树");
        loraVo.setKeyword(Arrays.asList("村上春树"));
        loraVos.add(loraVo);
        System.out.println(JSONObject.toJSONString(loraVos));

        String t = "[{\\\"content\\\":\\\"彩蛋\\\",\\\"keyword\\\":[\\\"村上春树\\\"],\\\"title\\\":\\\"村上春树\\\"}]";
        System.out.println(JSONObject.parseArray(t,loraVo.class));
    }


}
