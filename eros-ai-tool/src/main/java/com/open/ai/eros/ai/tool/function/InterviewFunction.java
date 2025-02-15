package com.open.ai.eros.ai.tool.function;

import com.open.ai.eros.common.constants.ReviewStatusEnums;
import com.open.ai.eros.common.util.DateUtils;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Date;

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
    RESUME_SCREENING(0, "resume_screening","简历初筛"),
    INVITATION_FOLLOW_UP(1, "invitation_follow","邀约跟进"),
    INTERVIEW_ARRANGEMENT(2, "interview_arrangement","面试安排"),
    OFFER_ISSUED(3, "offer_issued","发放offer"),
    ONBOARD(4, "onboard","已入职");
     *
     */
    @Tool(name = "set_status", value = {"判断当前沟通进行到了哪一步并调用本函数设置跟进状态,状态码对应的code如下, 跟进中:invitation_follow, 已安排面试:interview_arrangement, 无意向:abandon"})
    public String set_status(@P("设置沟通状态") String status) {

        // 定义返回结果
//        String resultMessage;

        // 根据状态执行不同的逻辑
//        switch (status) {
//            case "following":
//                resultMessage = "状态已更新为：跟进中";
//                break;
//            case "already":
//                resultMessage = "状态已更新为：已安排面试";
//                break;
//            case "abandon":
//                resultMessage = "状态已更新为：无意向";
//                break;
//        }

        // 返回结构化的 JSON 格式结果
        return status;
    }

}
