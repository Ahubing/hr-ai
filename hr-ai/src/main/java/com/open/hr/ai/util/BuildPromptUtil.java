package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.db.mysql.hr.entity.AmNewMask;
import com.open.ai.eros.db.mysql.hr.entity.AmPosition;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.hr.ai.bean.req.AmNewMaskAddReq;
import com.open.hr.ai.bean.req.CompanyInfo;
import com.open.hr.ai.bean.req.DifferentiationAdvantage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

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
            "4. 我们会在对话中与候选人交换联系方式，这是正常现象。这将由招聘平台自动完成，你可能会收到联系方式相关的文本信息，这是招聘平台自动发送的忽略即可。\n" ,
            "5. 你收到的xxx.pdf是招聘平台处理的文件，忽略即可。",
            "6. 和用户预约好面试后需要询问用户是否确定，得到用户回复后才算确认。你约定好面试后不会再有HR主动联系用户确认，你约定的就是最终结果。" ,
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


    private static List<String> userInfoPrompts = Arrays.asList(
            "# 候选人信息\n- 候选人姓名：{userName}\n" ,
            "# 候选人简历信息\n {zpData} \n",
            "# 候选人筛选提示词 根据下面的提示词对候选人进行筛选，如果不符合的调用工具函数标记为不符合————————————\n{filterWord}\n————————————");


    /**
     * 流程控制prompt
     */
    public static String processControlPrompt = "\n" +
            "# 沟通进度\n" +
            "沟通进度共有以下几种（进度必须按顺序，且不可倒退）：\n" +
            "1. 简历初筛：等待简历初筛（因为目前系统只有自动简历初筛，会根据筛选条件自动完成筛选，所以简历会直接进入下一阶段）\n" +
            "2. 业务筛选：进行意向确认，候选人有意向则使用“候选人筛选提示词”对候选人进行筛选（为空则不需要进行筛选），筛选通过后进入下一状态。无意向或未通过筛选进入“不符合”状态\n" +
            "3. 邀约跟进：预约面试（时间，地点）。\n" +
            "4. 等待面试：AI只需要跟进到这个阶段，后面的阶段由人工进行操作。\n" +
//            "5. 已发offer\n" +
//            "6. 已入职\n" +
            "5. 不符合：无意向或已被淘汰\n\n" +
            "当前所处进度：{currentType}\n";


    /**
     *  示例对话
     */
    private static String exampleDialog = "# 示例对话数据\n" +
            "————————————\n" +
            "{exampleDialog}\n" +
            "————————————";


    public static String buildPrompt(AmResume amResume, AmNewMask amNewMask) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String aiRequestParam = amNewMask.getAiRequestParam();
            if (StringUtils.isBlank(aiRequestParam)) {
                return null;
            } else {
                AmNewMaskAddReq amNewMaskAddReq = JSONObject.parseObject(aiRequestParam, AmNewMaskAddReq.class);

                // 提取基本的数据
                CompanyInfo companyInfo = amNewMaskAddReq.getCompanyInfo();
                Map<String, Object> companyObject = convertToMap(companyInfo);
                if (Objects.isNull(companyObject) ) {
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
                        }
                    }
                }
                // 差异化优势（可选，若没有则整个模块都不出现）
                if (Objects.nonNull(amNewMaskAddReq.getDifferentiatedAdvantagesSwitch()) && amNewMaskAddReq.getDifferentiatedAdvantagesSwitch()) {
                    DifferentiationAdvantage differentiationAdvantage = amNewMaskAddReq.getDifferentiationAdvantage();
                    Map<String, Object> differentiationAdvantageObject = convertToMap(differentiationAdvantage);
                    if (Objects.nonNull(differentiationAdvantageObject)) {
                        for (String provideStr : providePrompt) {
                            List<String> strings = VariableUtil.regexVariable(provideStr);
                            if (strings.isEmpty()){
                                stringBuilder.append(provideStr);
                                continue;
                            }
                            for (String string : strings) {
                                if (differentiationAdvantageObject.containsKey(string) && differentiationAdvantageObject.get(string) != null) {
                                    stringBuilder.append(provideStr.replace("{" + string + "}", differentiationAdvantageObject.get(string).toString()));
                                }
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

                for (String userInfoPrompt : userInfoPrompts) {
                    List<String> strings = VariableUtil.regexVariable(userInfoPrompt);
                    if (strings.isEmpty()){
                        stringBuilder.append(userInfoPrompt);
                        continue;
                    }
                    for (String string : strings) {
                        if (string.equals("zpData")) {
                            if (StringUtils.isNotBlank(amResume.getZpData())) {
                                stringBuilder.append(userInfoPrompt.replace("{zpData}", amResume.getZpData()));
                            }
                        }
                        if (string.equals("filterWord")) {
                            if (StringUtils.isNotBlank(amNewMaskAddReq.getFilterWords())){
                                stringBuilder.append(userInfoPrompt.replace("{filterWord}", amNewMaskAddReq.getFilterWords()));
                            }
                        }
                        if (string.equals("userName")) {
                            if (StringUtils.isNotBlank(amResume.getName())) {
                                stringBuilder.append(userInfoPrompt.replace("{userName}", amResume.getName()));
                            }
                        }

                    }
                }

                // 流程控制
                Integer type = amResume.getType();
                ReviewStatusEnums enumByStatus = ReviewStatusEnums.getEnumByStatus(type);
                stringBuilder.append(processControlPrompt.replace("{currentType}", enumByStatus.getDesc()));

                // 示例对话
                String exampleDialogs = amNewMaskAddReq.getExampleDialogues();
                if (StringUtils.isNotBlank(exampleDialogs)) {
                    stringBuilder.append(exampleDialog.replace("{exampleDialog}", exampleDialogs));
                }
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



//    public static void main(String[] args) {
//        AmNewMask amNewMask = new AmNewMask();
//        amNewMask.setAiRequestParam("{\"companyInfo\":{\"companyName\":\"阿里巴巴\",\"industry\":\"互联网\",\"establishedTime\":\"2000-01-01\",\"scale\":\"10000人以上\",\"headquartersLocation\":\"杭州\",\"officialWebsite\":\"www.ali.com\",\"jobName\":\"Java开发工程师\",\"locationName\":\"杭州\",\"workLocation\":\"西湖区\",\"workTime\":\"9:00-18:00\",\"workMiniTime\":\"1年\",\"jobTypeName\":\"全职\",\"salaryDesc\":\"10k-20k\",\"emergencyDegree\":\"紧急\"},\"differentiatedAdvantages\":true,\"openInterview\":true,\"interviewAddress\":\"广州天河的地址\",\"otherRecruitmentInfo\":\"其他招聘信息,谢谢谢谢谢谢谢谢\",\"style\":\"轻松写意\",\"otherArgue\":\"长得帅, 小白脸\"}\n");
//        AmResume amResume = new AmResume();
//        amResume.setName("张三");
//        AmPosition amPosition = new AmPosition();
//        amPosition.setExtendParams("{\"jobName\":\"数据标注\",\"locationName\":\"广州\",\"degreeName\":\"本科\",\"experienceName\":\"1年\",\"skillRequire\":\"java\",\"otherArgue\":\"长得帅就好\"}\n");
//        String s = buildPrompt(amResume, amNewMask);
//        System.out.println(s);
//    }

    public static void main(String[] args) {
        String inputTime = "23:56";
        long timestamp = DateUtils.convertToTimestamp(inputTime);
        System.out.println("今天 " + inputTime + " 的时间戳是：" + timestamp);
        LocalDate now = LocalDate.now();
        System.out.println("今天是：" + now);

    }
}
