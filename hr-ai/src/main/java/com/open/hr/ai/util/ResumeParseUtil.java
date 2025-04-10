package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.util.AIJsonUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2025/2/8 19:26
 */
@Slf4j
public class ResumeParseUtil {


    private static String systemPrompt = "你是一名专业的数据提取工程师,擅长从简历中提取出数据,并且根据用户的要求提取json格式的数据\n\n 要求不要输出与json数据无关的内容. 需要提取的json结构数据如下,未明确的json字段则该字段的内容直接输出null \n\n age 需要识别成数字,gender这个字段需要识别成数字, 男生为1,女生为0 以可以解析成json的格式输出给我,请严格按照下面的格式 \n\n ```json{\"name\":\"姓名\",\"company\":\"所在公司\",\"city\":\"所在城市\",\"position\":\"应聘岗位\",\"gender\":\"性别\",\"salary\":\"工资\",\"education\":[{\"school\":\"陕西开放大学\",\"major\":\"电气自动化技术\",\"degree\":\"高中|大专|大学|硕士|博士 \",\"time_range\":\"2022 - 2024\"}],\"age\":20,\"experiences\":[{\"company\":\"卓望信息科技有限公司\",\"position\":\"营运推广主管\",\"startDate\":\"2013.10\",\"endDate\":\"至今\",\"responsibilities\":[\"负责社会化搭建工作\"],\"achievements\":[\"社会化媒体账号\"]}],\"projects\":[{\"name\":\"stm32\",\"role\":\"自主编写\",\"time_range\":\"2025.02 - 2025.02 \",\"description\":\"项目描述\"}],\"applyStatus\":\"应聘状态,比如: 在校-考虑机会\",\"phone\":\"手机号\",\"wechat\":\"微信号\",\"email\":\"邮箱号\",\"workYears\":\"工作年限\",\"skills\":\"技能\"}```";
    private static String userPrompt = " 你好帮我分析这个文档,提取出简历信息, 不要输出其他无关的数据";

    public static List<ChatMessage> buildPrompt(String url) {
        try {

            // 构造系统提示词
            List<ChatMessage> newMessages = new ArrayList<>();
            ChatMessage systemMessage = new ChatMessage(AIRoleEnum.ASSISTANT.getRoleName(), systemPrompt);
            newMessages.add(systemMessage);
            // 拼接提示词
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(url);
            stringBuilder.append(userPrompt);
            String prompt = stringBuilder.toString();
            ChatMessage user = new ChatMessage(AIRoleEnum.USER.getRoleName(), prompt);
            newMessages.add(user);
            return newMessages;
        } catch (Exception e) {
            log.error("构建面具失败", e);
        }
        return null;
    }


    public static void main(String[] args) {
        String aiCode = "> retrieving file (file-7Xj2iweGjX64oJN5RZtDKU) ok ✅\n\n```json\n{\n  \"name\": \"奈森\",\n  \"company\": \"卓望信息科技有限公司\",\n  \"city\": \"广东省广州市\",\n  \"position\": \"市场专员\",\n  \"gender\": null,\n  \"salary\": null,\n  \"education\": [\n    {\n      \"school\": \"华南理工\",\n      \"major\": \"市场营销\",\n      \"degree\": null,\n      \"time_range\": \"2008.09 - 2012.07\"\n    }\n  ],\n  \"age\": 23,\n  \"experiences\": [\n    {\n      \"company\": \"卓望信息科技有限公司\",\n      \"position\": \"营运推广主管\",\n      \"startDate\": \"2013.10\",\n      \"endDate\": \"至今\",\n      \"responsibilities\": [\n        \"负责社会化媒体营销团队的搭建工作，制定相关运营策略和指标，带领团队实施计划\",\n        \"网站常态运营活动规划和推进执行\",\n        \"相关数据报告和统计，为公司决策层提供决策依据\",\n        \"轻量级产品和应用的策划，统筹产品、技术团队成员实施\"\n      ],\n      \"achievements\": [\n        \"社会化媒体账号总共涨粉67万（包含QQ空间，人人网，新浪微博，腾讯微博）\",\n        \"日均互动量相比接手前提升1000%\",\n        \"评论转发量级达到百千级\"\n      ]\n    },\n    {\n      \"company\": \"广州灵心沙文化活动有限公司\",\n      \"position\": \"市场推广专员\",\n      \"startDate\": \"2012.08\",\n      \"endDate\": \"2013.09\",\n      \"responsibilities\": [\n        \"网络推广渠道搭建维护，包括QQ空间、微博、豆瓣等\",\n        \"负责软硬广投放，网络舆情监控，公关稿撰写，事件营销策划\",\n        \"标书制作和撰写，甲方沟通工作\"\n      ],\n      \"achievements\": null\n    }\n  ],\n  \"projects\": [],\n  \"applyStatus\": \"在校-考虑机会\",\n  \"phone\": \"13500000000\",\n  \"wechat\": \"13500135000\",\n  \"email\": \"hr@500d.me\",\n  \"workYears\": null,\n  \"skills\": [\n    \"CET-6，优秀的听说写能力\",\n    \"计算机二级，熟悉计算机各项操作\",\n    \"高级营销员，国家职业资格四级\"\n  ]\n}\n```";
        String jsonContent = AIJsonUtil.getJsonContent(aiCode);
        System.out.println(jsonContent);
        AmResume jsonObject = JSONObject.parseObject(jsonContent, AmResume.class);
        System.out.println(jsonObject.toString());

    }
}
