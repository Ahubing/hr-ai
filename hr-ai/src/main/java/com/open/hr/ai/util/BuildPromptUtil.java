package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.db.mysql.hr.entity.AmNewMask;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.hr.ai.bean.req.AmNewMaskAddReq;
import com.open.hr.ai.bean.req.CompanyInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Date 2025/2/8 19:26
 */
@Slf4j
public class BuildPromptUtil {

    public static List<String> basePrompt = Arrays.asList("# 核心角色设定\n",
            "你是一位专业的人力资源AI助手，负责在招聘平台以专业且友好的方式吸引优质候选人。你的任务是模拟资深HR的沟通方式与候选人互动。\n" +
            "# 沟通注意事项\n" ,
            "1. 不要暴露你是AI助手，尽量模仿人类发送消息的风格。\n" ,
            "2. 不要虚构不存在的信息，如果用户问的问题你不清楚，就含糊过去。\n" ,
            "3. 不要使用markdown语法，招聘平台只支持纯文本内容。\n" ,
            "# 企业基础信息\n" ,
            "[公司名称]：{company}\n" ,
            "[行业领域]：{area}\n" ,
            "[成立时间]：{establishedTime}\n" ,
            "[公司规模]：{scale}\n" ,
            "[总部地点]：{headquartersLocation}\n" ,
            "[官方网站]：{officialWebsite}\n" ,
            "# 招聘职位\n" ,
            "[职位名称]：{jobName} \n" ,
            "[工作城市]：{locationName} \n" ,
            "[工作地点]：{workLocation}\n" ,
            "[工作时间]: {workTime}\n" ,
            "[最短工作时间要求]: {workMiniTime}\n" ,
            "[职位类型]：{jobTypeName}\n" ,
            "[薪资范围]：{salaryDesc}\n" ,
            "[招聘人数]：{recruitingNumbers}\n" ,
            "[紧急程度]：{emergencyDegree}\n" ,
            "# 职位要求\n" ,
            "【硬性要求】\n" ,
            "- 学历要求：{degreeName}\n" ,
            "- 工作经验：{experienceName}\n" ,
            "- 专业技能：{skillRequire}\n" ,
            "- 其他要求：{otherArgue}"
    );


    public static List<String> providePrompt =  Arrays.asList(
            "# 差异化优势\n" ,
            "【我们提供的】\n" ,
            "- 薪酬福利：{salaryAndWelfare}\n" ,
            "- 职业发展：{careerDevelopment}\n" ,
            "- 工作环境：{workEnvironment}\n" ,
            "- 特别福利：{welfare}\n" );

    public static String interviewPrompt = "# 面试信息\n - 面试方式：线下\n - 面试地址：{address}\n";

    public static String otherInformationPrompt = "# 其他招聘信息\n {otherInformation}\n";

    private static List<String> intelligentInteraction = Arrays.asList(
            "# 智能交互指令\n" ,
            "1. 内容生成原则：\n" ,
            "   1. 语气风格：{style}\n" ,
            "   2. 关键词密度：自然植入[行业关键词][职位关键词]\n" ,
            "   3. 转化引导：包含明确的CTA（立即申请/咨询详情）\n" ,
            "   4. 模拟人类：不要暴露你是AI助手，尽量模仿人类消息的风格。\n" ,
            "   5. 模糊不清楚的信息：不要虚构不存在的信息，如果用户问的问题你不清楚，就含糊过去。\n" ,
            "   6. 纯文本内容：不要使用markdown语法，招聘平台只支持纯文本内容。\n" ,
            "   7. 无关性原则：拒绝回复与招聘无关的内容\n" ,
            "2. 合规性要求：\n" ,
            "   1. 自动规避年龄/性别/地域等敏感信息\n" ,
            "   2. 符合当地劳动法规定\n" ,
            "   3. 体现DEI（多元化、公平、包容）原则\n");


    private static String userInfoPrompt = "# 候选人信息\n" +
            "\n" +
            "- 候选人姓名：{userName}\n" +
            "\n" +
            "# 候选人沟通脚本\n" +
            "\n" +
            "1. 确认是否感兴趣\n" +
            "2. 引导进入面试流程";


    public static String buildPrompt(AmResume amResume, AmNewMask amNewMask, AmPosition amPosition) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String aiRequestParam = amNewMask.getAiRequestParam();
            if (StringUtils.isBlank(aiRequestParam)) {
                return null;
            } else {
                AmNewMaskAddReq amNewMaskAddReq = JSONObject.parseObject(aiRequestParam, AmNewMaskAddReq.class);
                String extendParams = amPosition.getExtendParams();
                JSONObject positionObject = JSONObject.parseObject(extendParams);

                // 提取基本的数据
                CompanyInfo companyInfo = amNewMaskAddReq.getCompanyInfo();
                Map<String, Object> companyObject = convertToMap(companyInfo);
                if (Objects.isNull(companyObject) || Objects.isNull(positionObject)) {
                    log.info("公司信息或者职位信息为空");
                    return null;
                }

                // 提取公司信息,将companyInfo 装成map
                // 提取职位信息,将positionObject 装成map
                // 提取提取公司信息和职位 基本的数据
                for (String baseStr : basePrompt) {
                    List<String> strings = VariableUtil.regexVariable(baseStr);
                    if (strings.isEmpty()){
                        stringBuilder.append(baseStr);
                        continue;
                    }
                    for (String string : strings) {
                        if (companyObject.containsKey(string) && companyObject.get(string) != null) {
                            stringBuilder.append(baseStr.replace("{" + string + "}", companyObject.get(string).toString()));
                        } else if (positionObject.containsKey(string) && positionObject.get(string) != null) {
                            stringBuilder.append(baseStr.replace("{" + string + "}", positionObject.get(string).toString()));
                        }
                    }
                }

                // 差异化优势（可选，若没有则整个模块都不出现）
                if (Objects.nonNull(amNewMaskAddReq.getDifferentiatedAdvantages()) && amNewMaskAddReq.getDifferentiatedAdvantages()) {
                    for (String provideStr : providePrompt) {
                        List<String> strings = VariableUtil.regexVariable(provideStr);
                        if (strings.isEmpty()){
                            stringBuilder.append(provideStr);
                            continue;
                        }
                        for (String string : strings) {
                            if (companyObject.containsKey(string) && companyObject.get(string) != null) {
                                stringBuilder.append(provideStr.replace("{" + string + "}", companyObject.get(string).toString()));
                            } else if (positionObject.containsKey(string) && positionObject.get(string) != null) {
                                stringBuilder.append(provideStr.replace("{" + string + "}", positionObject.get(string).toString()));
                            }
                        }
                    }

                    String interviewAddress = amNewMaskAddReq.getInterviewAddress();
                    if (StringUtils.isNotBlank(interviewAddress)) {
                        stringBuilder.append(interviewPrompt.replace("{address}", interviewAddress));
                    }
                }

                // #其他招聘信息
                String otherRecruitmentInfo = amNewMaskAddReq.getOtherRecruitmentInfo();
                if (StringUtils.isNotBlank(otherRecruitmentInfo)) {
                    stringBuilder.append(otherInformationPrompt.replace("{otherInformation}", otherRecruitmentInfo));
                }

                // 智能交互指令
                String style = amNewMaskAddReq.getStyle();
                for (String s : intelligentInteraction) {
                    List<String> strings = VariableUtil.regexVariable(s);
                    if (strings.isEmpty()){
                        stringBuilder.append(s);
                        continue;
                    }
                    for (String string : strings) {
                        if (string.equals("style")) {
                            if (StringUtils.isNotBlank(style)) {
                                stringBuilder.append(s.replace("{" + string + "}", style));
                            }
                        } else {
                            stringBuilder.append(s);
                        }
                    }
                }
                // 候选人信息
                stringBuilder.append(userInfoPrompt.replace("{userName}", amResume.getName()));
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("构建面具失败", e);
        }
        return null;

    }




    public static Map<String, Object> convertToMap(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            Map<String, Object> map = new HashMap<>();
            // 获取对象的所有字段
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // 设置字段可访问
                map.put(field.getName(), field.get(obj)); // 将字段名和字段值放入Map
            }
            return map;
        } catch (Exception e) {
            log.error("转换失败", e);
        }
        return null;
    }



    public static void main(String[] args) {
        AmNewMask amNewMask = new AmNewMask();
        amNewMask.setAiRequestParam("{\"companyInfo\":{\"companyName\":\"阿里巴巴\",\"industry\":\"互联网\",\"establishedTime\":\"2000-01-01\",\"scale\":\"10000人以上\",\"headquartersLocation\":\"杭州\",\"officialWebsite\":\"www.ali.com\",\"jobName\":\"Java开发工程师\",\"locationName\":\"杭州\",\"workLocation\":\"西湖区\",\"workTime\":\"9:00-18:00\",\"workMiniTime\":\"1年\",\"jobTypeName\":\"全职\",\"salaryDesc\":\"10k-20k\",\"emergencyDegree\":\"紧急\"},\"differentiatedAdvantages\":true,\"openInterview\":true,\"interviewAddress\":\"广州天河的地址\",\"otherRecruitmentInfo\":\"其他招聘信息,谢谢谢谢谢谢谢谢\",\"style\":\"轻松写意\",\"otherArgue\":\"长得帅, 小白脸\"}\n");
        AmResume amResume = new AmResume();
        amResume.setName("张三");
        AmPosition amPosition = new AmPosition();
        amPosition.setExtendParams("{\"jobName\":\"数据标注\",\"locationName\":\"广州\",\"degreeName\":\"本科\",\"experienceName\":\"1年\",\"skillRequire\":\"java\",\"otherArgue\":\"长得帅就好\"}\n");
        String s = buildPrompt(amResume, amNewMask, amPosition);
        System.out.println(s);
    }
}
