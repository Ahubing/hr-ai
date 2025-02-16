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
        String aiCode = "> retrieving file (file-7Xj2iweGjX64oJN5RZtDKU) ok ✅\n\n```json\n{\n  \"name\": \"奈森\",\n  \"company\": \"卓望信息科技有限公司\",\n  \"city\": \"广东省广州市\",\n  \"position\": \"市场专员\",\n  \"gender\": null,\n  \"salary\": null,\n  \"education\": [\n    {\n      \"school\": \"华南理工\",\n      \"major\": \"市场营销\",\n      \"degree\": null,\n      \"time_range\": \"2008.09 - 2012.07\"\n    }\n  ],\n  \"age\": 23,\n  \"experiences\": [\n    {\n      \"company\": \"卓望信息科技有限公司\",\n      \"position\": \"营运推广主管\",\n      \"startDate\": \"2013.10\",\n      \"endDate\": \"至今\",\n      \"responsibilities\": [\n        \"负责社会化媒体营销团队的搭建工作，制定相关运营策略和指标，带领团队实施计划\",\n        \"网站常态运营活动规划和推进执行\",\n        \"相关数据报告和统计，为公司决策层提供决策依据\",\n        \"轻量级产品和应用的策划，统筹产品、技术团队成员实施\"\n      ],\n      \"achievements\": [\n        \"社会化媒体账号总共涨粉67万（包含QQ空间，人人网，新浪微博，腾讯微博）\",\n        \"日均互动量相比接手前提升1000%\",\n        \"评论转发量级达到百千级\"\n      ]\n    },\n    {\n      \"company\": \"广州灵心沙文化活动有限公司\",\n      \"position\": \"市场推广专员\",\n      \"startDate\": \"2012.08\",\n      \"endDate\": \"2013.09\",\n      \"responsibilities\": [\n        \"网络推广渠道搭建维护，包括QQ空间、微博、豆瓣等\",\n        \"负责软硬广投放，网络舆情监控，公关稿撰写，事件营销策划\",\n        \"标书制作和撰写，甲方沟通工作\"\n      ],\n      \"achievements\": null\n    }\n  ],\n  \"projects\": [],\n  \"applyStatus\": \"在校-考虑机会\",\n  \"phone\": \"13500000000\",\n  \"wechat\": \"13500135000\",\n  \"email\": \"hr@500d.me\",\n  \"workYears\": null,\n  \"skills\": [\n    \"CET-6，优秀的听说写能力\",\n    \"计算机二级，熟悉计算机各项操作\",\n    \"高级营销员，国家职业资格四级\"\n  ]\n}\n```";
        String jsonContent = getJsonContent(aiCode);
        System.out.println(jsonContent);
        JSONObject jsonObject = JSONObject.parseObject(jsonContent);
        Object data = jsonObject.get("experiences");
        System.out.println(data.toString());

    }

}
