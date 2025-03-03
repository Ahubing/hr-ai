package com.open.hr.ai.util;

import com.open.ai.eros.ai.manager.CommonAIManager;
import com.open.ai.eros.common.util.AIJsonUtil;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.db.constants.AIRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2025/3/3 00:51
 */
@Slf4j
@Component
public class CompetencyModelPromptUtil {


    @Resource
    private CommonAIManager commonAIManager;

    private static String JobDescriptionPrompt =  "{positionName}, 这个岗位对描述{amDescribe}, 请根据以上信息，帮我精准分析企业的人才需求，然后生成人才画像的详细标签，" +
            "以及岗位胜任力模型的评价标准和打分权重规则，用 json 的结构化信息输出给我，方便我在前端进行标准渲染,注意只需要输出json结构给我,不需要其他数据" +
            "参考结构如下: \n" +
            "{\"企业人才需求\":{\"岗位\":\"数据标注/AI训练师\",\"需求\":\"对图片和文本数据进行打标注\"},\"人才画像\":{\"详细标签\":{\"技能要求\":[\"数据标注\",\"AI训练\",\"图片处理\",\"文本处理\"],\"学历要求\":\"本科及以上\",\"工作经验要求\":\"1年及以上相关经验\",\"其他要求\":\"具备耐心细致的工作态度\"},\"岗位胜任力模型\":{\"评价标准\":{\"技能匹配度\":\"能否熟练进行数据标注和AI训练\",\"学历匹配度\":\"是否符合本科及以上学历要求\",\"工作经验匹配度\":\"是否有1年及以上相关经验\",\"工作态度匹配度\":\"是否具备耐心细致的工作态度\"},\"打分权重规则\":{\"技能匹配度\":0.4,\"学历匹配度\":0.2,\"工作经验匹配度\":0.3,\"工作态度匹配度\":0.1}}}}";


    private static String amResumeCompetencyModelPrompt = "人才画像的详细标签和以及岗位胜任力模型\n" +
            "{competencyModel} \n" +
            "用户简历如下: \n"+
            "{amResume} \n"+
            "请根据以上信息，帮我精准分析企业的人才需求，然后生成人才画像的详细标签，以及岗位胜任力模型的评价标准和打分权重规则，用 json 的结构化信息输出给我，方便我在前端进行标准渲染, 严格参考下面的json结构, 只需要给我json结构数据,不需要其他任何数据" +
            "参考结构如下: \n" +
            "{\"专业技能\":{\"分数\":85,\"合适程度\":\"非常合适\"},\"综合评估\":{\"专业能力\":70,\"学习能力\":85,\"团队协作\":70,\"问题解决\":85,\"创新能力\":70,\"沟通能力\":85},\"人才画像\":{\"优势特征\":[\"具备扎实的Java编程能力和Spring框架应用经验\",\"熟练使用MySQL和Redis\",\"具备Netty网络编程和Spring Cloud组件应用经验\"],\"风险特征\":[\"对JVM底层原理理解深度和RocketMQ消息中间件应用经验有待提升\"]},\"智能摘要\":{\"人才评估建议\":[\"该候选人综合得分为86分,位于 非常合适 等级。具备优秀的学习能力,能够快速适应新环境和技术要求。技能基础扎实,有较强的团队协作和问题解决能力。建议进一步了解其沟通表达能力\"],\"技能能力\":[\"专业知识体系完整,技术基础扎实且有实践应用能力。对新支术有很强的学习和应用能力。具备创新思维,能理解技术优化建议\"],\"管理建议\":[\"可继续承担核心研发工作,快速形成生产力。建议安排参与技术难度较高的项目。可考虑培养为技术骨干,跨过3-6个月可以承担技术Leader角色。有潜力承担小型团队技术Leader角色。\"]}}";


    private String BuildAiJobDescription(String positionName,String amDescribe ){
        String replace = JobDescriptionPrompt.replace("{positionName}", positionName);
        String buildAiJobDescriptionPrompt = replace.replace("{amDescribe}", amDescribe);
        return buildAiJobDescriptionPrompt;
    }



    private String BuildAmResumeCompetencyModel(String competencyModel,String amResume ){
        String replace = amResumeCompetencyModelPrompt.replace("{competencyModel}", competencyModel);
        String buildAmResumeCompetencyModel = replace.replace("{amResume}", amResume);
        return buildAmResumeCompetencyModel;
    }


    public List<ChatMessage> buildPrompt(String prompt) {
        try {
            // 构造系统提示词
            List<ChatMessage> newMessages = new ArrayList<>();
            ChatMessage user = new ChatMessage(AIRoleEnum.USER.getRoleName(), prompt);
            newMessages.add(user);
            return newMessages;
        } catch (Exception e) {
            log.error("构建面具失败", e);
        }
        return null;
    }


    public String dealJobDescription(String positionName,String amDescribe ){
        String prompt = BuildAiJobDescription(positionName, amDescribe);
        List<ChatMessage> chatMessages = buildPrompt(prompt);
        String aiResult = "";
        for (int i = 0; i < 10; i++) {
            aiResult = commonAIManager.aiNoStreamWith(chatMessages, "OpenAI:gpt-4o-2024-05-13", 0.8);
            if (StringUtils.isNotBlank(aiResult)){
                break;
            }
        }
        if (StringUtils.isBlank(aiResult)){
            return null;
        }
        return  AIJsonUtil.getJsonContent(aiResult);
    }

    public String dealAmResumeCompetencyModel(String competencyModel,String amResume ){
        String prompt = BuildAmResumeCompetencyModel(competencyModel, amResume);
        List<ChatMessage> chatMessages = buildPrompt(prompt);
        String aiResult = "";
        for (int i = 0; i < 10; i++) {
            aiResult = commonAIManager.aiNoStreamWith(chatMessages, "OpenAI:gpt-4o-2024-05-13", 0.8);
            if (StringUtils.isNotBlank(aiResult)){
                break;
            }
        }
          if (StringUtils.isBlank(aiResult)){
            return null;
        }
        return AIJsonUtil.getJsonContent(aiResult);
    }


}
