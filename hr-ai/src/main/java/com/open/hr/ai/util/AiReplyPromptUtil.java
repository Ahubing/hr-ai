package com.open.hr.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.db.mysql.hr.entity.AmNewMask;
import com.open.ai.eros.db.mysql.hr.entity.AmResume;
import com.open.ai.eros.db.mysql.hr.entity.IcRecord;
import com.open.hr.ai.bean.req.AmNewMaskAddReq;
import com.open.hr.ai.bean.req.CompanyInfo;
import com.open.hr.ai.bean.req.DifferentiationAdvantage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Date 2025/2/8 19:26
 */
@Slf4j
public class AiReplyPromptUtil {


    public static final String firstPrompt  = "# 核心角色设定\n"+
            " 你是一位专业的人力资源AI助手，负责通过在线招聘平台以专业且友好的方式吸引优质求职者。你的任务是模拟资深HR的沟通方式与求职者互动。\n" ;
    public static final String baseInfoPrompt =   "# 回复格式\n" +
                    "你需要使用JSON格式来对候选人进行回复，软件会自动解析数据格式发送给求职者。JSON格式如下：\n" +
                    "{\"messages\":[\"你的简历符合我们的招聘要求\", \"我们可以约一个时间面试\", \"你看一下这周什么时间方便？\"]}\n" +
                    "参数解释: \n" +
            "messages是发送给候选人的消息，你可以模仿人类发消息时往往分成几段来发送。你可以让messages为空数组，代表不发送消息。\n\n" ;

    public static final List<String> basePrompt = Arrays.asList( "# 沟通注意事项\n" ,
            "1. 不要暴露你是AI助手，尽量模仿人类发送消息的风格，尽量简短，少使用换行符，不要过于礼貌。\n" +
            "2. 不要虚构不存在的信息，如果用户问的问题你不清楚，就含糊过去。\n" +
            "3. 不要使用markdown语法，招聘平台只支持纯文本内容。\n" +
            "4. 我们会在对话中与求职者交换联系方式，这是正常现象。这将由招聘平台自动完成，你可能会收到联系方式相关的文本信息，这是招聘平台自动发送的忽略即可。\n" +
            "5. 你收到的xxx.pdf是招聘平台处理的文件，忽略即可。\n" +
            "6. 和用户预约好面试后需要询问用户是否确定，得到用户回复后才算确认。你约定好面试后不会再有HR主动联系用户确认，你约定的就是最终结果。\n" +
            "7. 你一次只能回复一条消息，所以回复的结尾应该具有引导性，而不是简单的陈述信息。绝对不要回复求职者“请稍等”、“我会尽快给你反馈”等类似内容。你无法主动发起第二次对话。\n" +
            "8. 你可以在候选人发送客套话、脱离招聘内容、没有明确意义、侮辱性言语等内容或你无法继续回答时选择不回复候选人消息。\n" +
            "9. 为用户预约面试时如果对方没有给出明确时间则主动提供最近7天的可预约面试时间。\n",
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
            "- 岗位职责：{responsibilities}\n" ,
            "- 其他要求：{otherArgue}\n"
    );


    public static final List<String> providePrompt =  Arrays.asList(
            "# 差异化优势\n" ,
            "【我们提供的】\n" ,
            "- 薪酬福利：{salaryAndWelfare}\n" ,
            "- 职业发展：{careerDevelopment}\n" ,
            "- 工作环境：{workEnvironment}\n" ,
            "- 特别福利：{welfare}\n" );

    public static final String interviewPrompt = "# 面试信息\n" +
            "如果当前无已预约面试，求职者提出修改面试时间则直接重新预约面试。\n"  +
            "- 面试方式：{address} \n";


    public static final String currentTypePrompt =
            "# 当前状态\n" +
            "- 系统查询到的已预约的面试：{interview_info} \n"+
            "- 当前所处进度：{currentType}\n" +
            "- 当前招聘平台：{platform}\n";

    public static final String otherInformationPrompt = "# 其他招聘信息\n {otherInformation}\n";

    private static final List<String> intelligentInteraction = Arrays.asList(
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


    private static final String userFilterPrompts =  "# 求职者筛选提示词\n 根据下面的提示词对求职者进行筛选，如果不符合的调用工具函数标记为不符合\n————————————\n{filterWord}\n————————————\n";


    private static final List<String> userInfoPrompts = Arrays.asList(
            "# 求职者信息\n- 求职者姓名：{userName}\n" +
            "# 求职者简历信息（这只是平台提供的线简历信息，不是求职者发送的简历）\n" ,
            "# 求职者简历信息\n {zpData} \n");

    /**
     * 流程控制prompt
     */
    public static final String processControlPrompt = "\n" +
            "# 沟通进度\n" +
            "沟通进度共有以下几种（进度必须按顺序，且不可倒退）：\n" +
            "1. 简历初筛：等待简历初筛\n" +
            "2. 业务筛选：进行意向确认，求职者有意向则使用“求职者筛选提示词”对求职者进行筛选（为空则不需要进行筛选），筛选通过后进入下一状态。无意向或未通过筛选进入“不符合”状态\n" +
            "3. 邀约跟进：预约面试（时间，地点）。\n" +
            "4. 等待面试：进入条件：已经约定好面试（时间，地点）；进入后动作：向求职者发送完整面试信息，并提示按时参加\n" +
//            "5. 已发offer\n" +
//            "6. 已入职\n" +
            "5. 不符合：无意向或已被淘汰\n\n" ;

    /**
     * 工具调用prompt
     */

    public static final String toolPrompt = "\n" +
            "# 工具调用说明\n" +
            "\n" +
            "## 工具调用优先级\n" +
            "\n" +
            "你只能选择一个工具进行调用，请根据情景选择当前需要且优先级最高的工具进行调用(优先级顺序从高到低排列)。\n" +
            "\n" +
            "modify_interview_time\n" +
            "cancel_interview\n" +
            "appoint_interview\n" +
            "get_spare_time\n" +
            "set_status\n";

    private static String communicationScript =
            "# 候选人沟通脚本\n" +
            "{CommunicationScript}\n";


    /**
     *  示例对话
     */
    private static final String exampleDialog1 = "# 示例对话\n" +
            "## 基础设置\n" +
            "### 智能称呼\n" +
            "场景说明：自动识别求职者姓氏并在跟进中亲切称呼，如\"黄先生\"\n" +
            "男性称呼为“帅哥”\n" +
            "女性称呼为“美女 ”\n" +
            "并替换示例对话中的[\\智能称呼]部分\n" +
            "## 基础场景\n" +
            "### 求职者主动打招呼\n" +
            "场景说明：回复主动沟通的求职者\n" +
            "第一句：我们这边是从事做招聘文员的，工作轻松简单，上班打打电话，上班时间：十点到六点，工资：6000-12000\n" +
            "### 不符合要求的主动求职者\n" +
            "场景说明：对不符合要求的主动沟通求职者标记不合适和回复\n" +
            "第一句：感谢您的关注，看了您的简历不太匹配我们的工作岗位，祝您在BOSS上找到更匹配的工作。\n" +
            "### 拿到简历后回复\n" +
            "场景说明：拿到简历后统一回复或者统一询问\n" +
            "第一句：好的，收到了\n" +
            "### 求职者需要考虑\n" +
            "场景说明：求职者在沟通过程表示需要考虑一下时的回复\n" +
            "第一句：咱们可以交换下微信联系，有需要您可以微信咨询我~\n" +
            "第二句：那您这边考虑好了，可以直接加我微信了解待遇\n" +
            "### 拿到微信后回复\n" +
            "场景说明：在拿到微信后对应的沟通话术\n" +
            "第一句：那我们微信上联系，到时记得加我一下。\n" +
            "## 常规场景\n" +
            "### 公司概况\n" +
            "场景说明：你们公司是做什么的，规模实力是怎样的？\n" +
            "第一句：你好 我们是哔哩哔哩音频直播，唠嗑聊天 唱歌互动 讲故事 讲笑话 互动小游戏 听歌交友 话题绿色健康 声音好听\n" +
            "### 部门概况\n" +
            "场景说明：这个部门有多少人，整体情况是怎么样的？\n" +
            "第一句：我们是电台直播 电台分为聊天电台和唱见电台，也有视频主播需要颜值设备经验才艺展示，不知道您更倾向于哪种直播呢？ \n" +
            "### 工作内容\n" +
            "场景说明：方便介绍一下岗位吗，日常的工作形式和工作内容具体是怎样的？\n" +
            "第一句：每天上班接听一下电话，上班摸摸鱼\n" +
            "第二句：询问这个清楚了吗？\n" +
            "### 工作时间\n" +
            "场景说明：上班时间和休息时间是怎样的？单休还是双休？节假日？\n" +
            "第一句：早上十点，到晚上六点，上六休一\n" +
            "第二句：这个工作时间可以接受吗？\n" +
            "### 加班\n" +
            "场景说明：平时加班吗？加班多吗？一般加班到几点？\n" +
            "第一句：加班的话，一般是到9:00多。\n" +
            "第二句：销售岗位主要是以团队业绩为目标的，当天团队完成业绩目标，就可以准时下班，没有完成的话，就需要加班。\n" +
            "第三句：这个清楚了吗？\n" +
            "### 节假日\n" +
            "场景说明：你们单休还是双休？节假日怎么放？\n" +
            "第一句：我们公司是单休的，固定休息周日，节假日正常放假的\n" +
            "### 薪酬\n" +
            "场景说明：工资待遇怎么样的？试用期有多少？大概收入在什么水平？\n" +
            "第一句：薪资是5000元无责底薪+提成（试用期2个月，4000元无责底薪+提成），基本收入是在9-15K。\n" +
            "第二句：这个薪酬有在你预期范围内吗？\n" +
            "### 五险一金\n" +
            "场景说明：有五险一金吗？一入职（试用期就购买吗？\n" +
            "第一句：我们这边有五险一金，转正后购买。\n" +
            "### 福利-食宿补贴\n" +
            "场景说明：公司提供住宿吗？包吃住吗？有其他补贴福利吗？\n" +
            "第一句：我们这边包吃包住，有通勤、通讯、差旅和高温等各种补贴，每年免费旅游，以及各种员工活动（聚餐、郊游、运动会、员工生日会等）等着你的参与。\n" +
            "## 岗位要求\n" +
            "场景说明：工作要求？对经验和学历有什么要求吗？高中无经验可以吗？\n" +
            "第一句：没有太多要求，在家直播，电脑手机都可以直播 音频直播不露脸。有才艺会唱歌优先，每一种声音都会有人欣赏。\n" +
            "## 业绩要求\n" +
            "场景说明：每一个月有业绩要求吗？有考核吗？KPI是多少？\n" +
            "第一句：每个月有1.5w的标准线，不强制考核。\n" +
            "### 试用期\n" +
            "场景说明：试用期多久，多久能转正？转正有什么要求？\n" +
            "第一句：试用期转正后一到三个月\n" +
            "### 培训\n" +
            "场景说明：有培训吗，是带薪的吗？我没有什么经验，会有人带吗，带多久？\n" +
            "第一句：我们有专业的运营手把手培训，都有总结教学，让我们快速提升专业知识。\n" +
            "### 是否招聘实习\n" +
            "场景说明：请问这个职位招实习生吗？\n" +
            "第一句：需要的\n" +
            "## 尝试挽回的拒绝场景\n" +
            "场景说明：在候选人因为距离问题拒绝时进行礼貌回复或挽回\n" +
            "第一句：可以线上在家直播的，不需要到公司直播，您看还有哪些顾虑呢？\n" +
            "### 拒绝场景-薪酬\n" +
            "场景说明：在候选人因为薪酬问题拒绝时进行礼貌回复或挽回\n" +
            "第一句：我们综合薪酬是很高的，而且上不设限。后期也会根据个人能力和上级的考核评估不断提升~\n" +
            "### 拒绝场景-工作时间\n" +
            "场景说明：在候选人因为工作时间问题拒绝时进行礼貌回复或挽回\n" +
            "第一句：我们只需要播够3小时就好了，时间非常的灵活，自己规划\n" +
            "### 拒绝场景-行业职位\n" +
            "场景说明：在候选人因为行业职位问题拒绝时进行礼貌回复或挽回\n" +
            "第一句：我们这边还有很多其他在招的职位，你想找哪一类型的工作呢？你可以看一下我们公司其他职位有没有合适的~\n" ;

    /**
     *  示例对话
     */
    private static final String exampleDialog2 =
           "# 示例对话2\n" +
            "————————————\n" +
            "{exampleDialog}\n" +
            "————————————";


    public static String buildBasePrompt(AmResume amResume, AmNewMask amNewMask, IcRecord icRecord) {
        try {
            log.info("buildPrompt icRecord={}", JSONObject.toJSONString(icRecord));
            StringBuilder stringBuilder = new StringBuilder();
            String aiRequestParam = amNewMask.getAiRequestParam();
            if (StringUtils.isBlank(aiRequestParam)) {
                return null;
            } else {
                stringBuilder.append(firstPrompt);
                AmNewMaskAddReq amNewMaskAddReq = JSONObject.parseObject(aiRequestParam, AmNewMaskAddReq.class);
                log.info("amNewMaskAddReq:{}", JSONObject.toJSONString(aiRequestParam));

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

                }
                if(amNewMaskAddReq.getOpenInterviewSwitch()){
                    String dynamicInterviewPrompt = interviewPrompt;
                    String interviewAddress = amNewMaskAddReq.getInterviewAddress();
                    dynamicInterviewPrompt =  dynamicInterviewPrompt.replace("{address}", interviewAddress);
                    stringBuilder.append(dynamicInterviewPrompt);
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
                stringBuilder.append(processControlPrompt);
                    if (StringUtils.isNotBlank(amNewMaskAddReq.getFilterWords())){
                        stringBuilder.append(userFilterPrompts.replace("{filterWord}", amNewMaskAddReq.getFilterWords()));
                    }
                String communicationScriptStr = communicationScript.replace("{CommunicationScript}", amNewMaskAddReq.getCommunicationScript());
                stringBuilder.append(communicationScriptStr);
                // 示例对话
                stringBuilder.append(exampleDialog1);
                String exampleDialogs = amNewMaskAddReq.getExampleDialogues();
                if (StringUtils.isNotBlank(exampleDialogs)) {
                    stringBuilder.append(exampleDialog2.replace("{exampleDialog}", exampleDialogs));
                }
                //工具调用
                stringBuilder.append(toolPrompt);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("构建面具失败", e);
        }
        return null;

    }



    public static String buildCandidateBasePrompt(AmResume amResume, AmNewMask amNewMask, IcRecord icRecord) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String aiRequestParam = amNewMask.getAiRequestParam();
            if (StringUtils.isBlank(aiRequestParam)) {
                return null;
            } else {
                AmNewMaskAddReq amNewMaskAddReq = JSONObject.parseObject(aiRequestParam, AmNewMaskAddReq.class);
                log.info("amNewMaskAddReq:{}", JSONObject.toJSONString(aiRequestParam));

                // 求职者信息
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

                        if (string.equals("userName")) {
                            if (StringUtils.isNotBlank(amResume.getName())) {
                                stringBuilder.append(userInfoPrompt.replace("{userName}", amResume.getName()));
                            }
                        }

                    }
                }
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("构建求职者消息失败", e);
        }
        return null;

    }

    public static String buildFormatAndICRecordPrompt(AmResume amResume, AmNewMask amNewMask, IcRecord icRecord) {
        try {
            log.info("buildPrompt icRecord={}", JSONObject.toJSONString(icRecord));
            StringBuilder stringBuilder = new StringBuilder();
            String aiRequestParam = amNewMask.getAiRequestParam();
            if (StringUtils.isBlank(aiRequestParam)) {
                return null;
            } else {
                stringBuilder.append(baseInfoPrompt);
                AmNewMaskAddReq amNewMaskAddReq = JSONObject.parseObject(aiRequestParam, AmNewMaskAddReq.class);
                log.info("amNewMaskAddReq:{}", JSONObject.toJSONString(aiRequestParam));

                //面试信息
                if(amNewMaskAddReq.getOpenInterviewSwitch()){
                    log.info("before interview_info：{}",stringBuilder);
                    String dynamicInterviewPrompt = currentTypePrompt;
                     log.info("buildPrompt icRecord={} isnull?:{}", JSONObject.toJSONString(icRecord),icRecord == null);
                    if(Objects.nonNull(icRecord)){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("interviewId",icRecord.getId());
                        jsonObject.put("interviewTime", icRecord.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        dynamicInterviewPrompt =  dynamicInterviewPrompt.replace("{interview_info}", JSONObject.toJSONString(jsonObject));
                    }else {
                        dynamicInterviewPrompt =  dynamicInterviewPrompt.replace("{interview_info}", "");
                    }
                    Integer type = amResume.getType();
                    ReviewStatusEnums enumByStatus = ReviewStatusEnums.getEnumByStatus(type);
                    dynamicInterviewPrompt =  dynamicInterviewPrompt.replace("{currentType}", enumByStatus.getDesc());
                    dynamicInterviewPrompt =  dynamicInterviewPrompt.replace("{platform}", amResume.getPlatform());

                    stringBuilder.append(dynamicInterviewPrompt);
                    log.info("after interview_info：{}",stringBuilder);
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



    public static void main(String[] args) {
//        AmNewMask amNewMask = new AmNewMask();
//        amNewMask.setAiRequestParam("{\"companyInfo\":{\"companyName\":\"阿里巴巴\",\"industry\":\"互联网\",\"establishedTime\":\"2000-01-01\",\"scale\":\"10000人以上\",\"headquartersLocation\":\"杭州\",\"officialWebsite\":\"www.ali.com\",\"jobName\":\"Java开发工程师\",\"locationName\":\"杭州\",\"workLocation\":\"西湖区\",\"workTime\":\"9:00-18:00\",\"workMiniTime\":\"1年\",\"jobTypeName\":\"全职\",\"salaryDesc\":\"10k-20k\",\"emergencyDegree\":\"紧急\"},\"differentiatedAdvantages\":true,\"openInterview\":true,\"interviewAddress\":\"广州天河的地址\",\"otherRecruitmentInfo\":\"其他招聘信息,谢谢谢谢谢谢谢谢\",\"style\":\"轻松写意\",\"otherArgue\":\"长得帅, 小白脸\"}\n");
//        AmResume amResume = new AmResume();
//        amResume.setName("张三");
//        AmPosition amPosition = new AmPosition();
//        amPosition.setExtendParams("{\"jobName\":\"数据标注\",\"locationName\":\"广州\",\"degreeName\":\"本科\",\"experienceName\":\"1年\",\"skillRequire\":\"java\",\"otherArgue\":\"长得帅就好\"}\n");
//        String s = buildPrompt(amResume, amNewMask,null);
//        System.out.println(s);
//        amPosition.setExtendParams("{\"jobName\":\"数据标注2\",\"locationName\":\"广州2\",\"degreeName\":\"本2科\",\"experienceName\":\"1年\",\"skillRequire\":\"java\",\"otherArgue\":\"长得22帅就好\"}\n");
//        amNewMask.setAiRequestParam("{\"companyInfo\":{\"companyName\":\"阿里巴巴22\",\"industry\":\"互联网22\",\"establishedTime\":\"2000-01-0122\",\"scale\":\"10000人2以上\",\"headquartersLocation\":\"杭222州\",\"officialWebsite\":\"www.ali.com\",\"jobName\":\"Java开发工程师222\",\"locationName\":\"杭州222\",\"workLocation\":\"西湖区222\",\"workTime\":\"9:00-18:00\",\"workMiniTime\":\"1年\",\"jobTypeName\":\"全职\",\"salaryDesc\":\"10k-20k\",\"emergencyDegree\":\"紧急\"},\"differentiatedAdvantages\":true,\"openInterview\":true,\"interviewAddress\":\"广州天河的地址\",\"otherRecruitmentInfo\":\"其他招聘信息,谢谢谢谢谢谢谢谢\",\"style\":\"轻松写意\",\"otherArgue\":\"长得帅, 小白脸\"}\n");
//        String localOtherInformationPrompt = otherInformationPrompt;
//        String s2 = buildPrompt(amResume, amNewMask,null);
//        System.out.println(s2);
    }

//    public static void main(String[] args) {
//        String inputTime = "23:56";
//        long timestamp = DateUtils.convertToTimestamp(inputTime);
//        System.out.println("今天 " + inputTime + " 的时间戳是：" + timestamp);
//        LocalDate now = LocalDate.now();
//        System.out.println("今天是：" + now);
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("name", "张三");
//        jsonObject.put("age", 18);
//        jsonObject.put("address", "广州");
//        System.out.println(JSONObject.toJSONString(jsonObject));
//
//    }
}
