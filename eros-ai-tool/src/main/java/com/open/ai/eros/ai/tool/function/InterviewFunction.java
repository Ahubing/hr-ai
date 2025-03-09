package com.open.ai.eros.ai.tool.function;

import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.util.DateUtils;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @类名：TimeTool
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/15 0:28
 */
@Component
public class InterviewFunction {


    /**
     * 设置状态
     * 判断当前沟通进行到了哪一步并调用本函数设置跟进状态。
     *
     * @param status 当前状态（跟进中、已安排面试、无意向）
     * @return 返回一个结构化的 JSON 字符串，包含状态、时间和结果
     *
    ABANDON(-1, "abandon","不符合"),
    RESUME_SCREENING(0, "resume_screening","简历初筛"),
    BUSINESS_SCREENING(1, "business_screening","业务筛选"),
    INVITATION_FOLLOW_UP(2, "invitation_follow","邀约跟进"),
    INTERVIEW_ARRANGEMENT(3, "interview_arrangement","面试安排"),
    OFFER_ISSUED(4, "offer_issued","发放offer"),
    ONBOARD(5, "onboard","已入职");
     *
     */
    @Tool(name = "set_status", value = {"判断当前沟通进行到了哪一步并调用本函数设置跟进状态,状态码对应的code如下, 业务筛选:business_screening, 邀约跟进:invitation_follow,  面试安排:interview_arrangement, 不符合:abandon"})
    public String set_status(@P("设置沟通状态") String status) {

        // 注意 AI只需要跟进到这个阶段，后面的阶段由人工进行操作
        ReviewStatusEnums anEnum = ReviewStatusEnums.getEnum(status);
        if (Objects.nonNull(anEnum) && (anEnum.getStatus() > 3 )) {
            return ReviewStatusEnums.INTERVIEW_ARRANGEMENT.getKey();
        }

        // 返回结构化的 JSON 格式结果
        return status;
    }

    /**
     * 判断是否需要继续回复客户
     * @param status
     * @return
     */
    @Tool(name = "checkNeedReply", value = {"请"})
    public String checkNeedReply(@P("设置沟通状态") String status) {

        // 注意 AI只需要跟进到这个阶段，后面的阶段由人工进行操作
        ReviewStatusEnums anEnum = ReviewStatusEnums.getEnum(status);
        if (Objects.nonNull(anEnum) && (anEnum.getStatus() > 3 )) {
            return ReviewStatusEnums.INTERVIEW_ARRANGEMENT.getKey();
        }

        // 返回结构化的 JSON 格式结果
        return status;
    }



}
