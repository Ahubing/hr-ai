package com.open.ai.eros.ai.tool.function;

import com.open.ai.eros.db.mysql.text.entity.Sop;
import com.open.ai.eros.db.mysql.text.service.impl.SopServiceImpl;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @类名：SopFunction
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/16 23:26
 */
@Component
@Slf4j
public class SopFunction {


    @Autowired
    private SopServiceImpl sopService;


    @Tool(name = "query_sop_by_scene",value = {"物业场景分析详情的分类表,其中通过分析用户的上下文,获取到相应的分类编码.","Retrieve the standard 0perating Procedure (SOP) based on a specific scenario code. Use this when you need soP details for a given scene."})
    public String query_sop_by_scene(@P("The encoded representation of the scenario for which the SOP is being queried.") String scene_code){
        log.info("query_sop_by_scene scene_code={}",scene_code);
        if(StringUtils.isEmpty(scene_code)){
            return null;
        }
        //return "【任务流程 - 文明养宠 -养宠卫生】\n" +
        //        "1、如果客户提及狗便、狗尿等卫生问题，生成环境类工单，由保洁人员处理；要回应业主“您好，我马上安排保洁人员前去清理，并持续进行文明养犬的宣传。”，并终止流程。\n" +
        //        "【任务流程 - 文明养宠 -养宠安全】\n" +
        //        "1.如是未栓绳、宠物扰民等安全问题，特别强调“立即”安排秩序同事现场处理，要回应业主“您好业主，我马上安排秩序同事前去制止，并加强巡查，遇到类似问题及时提醒业主，并加强文明养犬的宣传。”并终止流程。\n" +
        //        "【任务流程 - 文明养宠 -流浪宠物】\n" +
        //        "1.当客户提出园区流浪宠物多，需要清理或采取措施的，要生成客服类工单，并回应业主“您好业主，我先进行反馈，并沟通具体解决方案。”并终止流程。";
        Sop sopBuSceneCode = sopService.getSopBuSceneCode(scene_code);
        return sopBuSceneCode==null?null:sopBuSceneCode.getSop();
    }



}
