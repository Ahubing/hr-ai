package com.open.ai.eros.ai.lang.chain.test.tool;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.tool.function.InterviewFunction;
import com.open.ai.eros.ai.tool.function.SopFunction;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.tool.DefaultToolExecutor;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @类名：FunctionCallByToolSpecification
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/13 15:59
 */
public class WuYeFunctionCall {


    public static void main(String[] args) {
        wuyew("setStatus");
    }



    public static void wuye(@MemoryId String firstLevelClassification) {

        SopFunction calculator = new SopFunction();
        Method[] methods = calculator.getClass().getMethods();

        Map<String,DefaultToolExecutor> toolExecutorMap = new HashMap<>();
        List<ToolSpecification> toolSpecifications = new ArrayList<>();
        for (Method method : methods) {
            if(method.isAnnotationPresent(Tool.class)){
                Tool annotation = method.getAnnotation(Tool.class);
                toolSpecifications.add(ToolSpecifications.toolSpecificationFrom(method));
                DefaultToolExecutor defaultToolExecutor = new DefaultToolExecutor(calculator, method);
                toolExecutorMap.put(annotation.name(),defaultToolExecutor);
            }
        }

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                .baseUrl("https://zen-vip.zeabur.app/v1")
                .modelName("gpt-4o-2024-05-13")
                .build();

        List<ChatMessage> messages = new ArrayList<>();

         SystemMessage systemMessage = new SystemMessage("身份定义：你是一名智能房地产管家，服务业主。以人类管家的口吻与业主进行对话。能力定义：你具备工单生成、知识库查询以及执行其他操作的能力。这些能力通过工具调用实现。\n\n场景定义：\n附录包含了场景细分编码表，当与用户的对话符合场景细分编码表的场景定义时需要按照以下流程操作。\n1. 选出一个最符合的场景，调用工具查询该场景的SOP\n2. 按照SOP进行操作，并询问生成工单需要的信息。如果需要派遣人员，需要询问一下具体的位置信息。如果需要人工上门，需要询问预约的时间。\n3. 生成工单。\n\n知识问答：\n当用户询问你知识性问题时，你需要调用工具查询知识库，如果未查询到相关内容则回答不知道，而不能编造内容。\n\n对话示例：\n你需要扮演人类管家以人类的口吻回复用户，示例如下\n业主：小周，我看有些业主手里拿着绳子，但是狗绳都不栓，见过好的多次了，建议出门拴着，大多数狗是乖，但是要避免意外，不怕一万就怕万一。\n管家：您好业主，我们加强巡查，遇到类似问题及时提醒业主，并加强文明养宠的宣传。\n业主：提醒一下吧，不然真的很危险\n管家：嗯嗯，是的。我们给全体邻居发送养宠文明注意事项，同时在公示栏公开倡导。\n\n附录：场景细分编码表\n| 一级分类编码 | 一级分类描述 | 二级分类 | 二级分类描述         | 三级分类 | 三级分类描述   | 场景定义  |\n| :----------- | :----------- | :------- | :------------------- | :------- | :------------- | ------------------------------------------------------------ |\n| YQ           | 园区         | YQ01     | 门岗放行             | YQ0101   | 业主放行       |  |\n| YQ           | 园区         | YQ01     | 门岗放行             | YQ0102   | 外来人员放行   |  |\n| YQ           | 园区         | YQ01     | 门岗放行             | YQ0103   | 施工人员放行   |  |\n| YQ           | 园区         | YQ01     | 门岗放行             | YQ0104   | 搬家放行       |  |\n| YQ           | 园区         | YQ02     | 公共区域文明行为管理 | YQ0201   | 噪音扰民       |  |\n| YQ           | 园区         | YQ02     | 公共区域文明行为管理 | YQ0202   | 高空抛物       |  |\n| YQ           | 园区         | YQ02     | 公共区域文明行为管理 | YQ0203   | 物品丢失       |  |\n| YQ           | 园区         | YQ02     | 公共区域文明行为管理 | YQ0204   | 文明养宠       | 解决园区内关于宠物的不文明行为，如狗便、狗尿不随手清理、未栓绳、宠物扰民、流浪宠物等问题 |\n| YQ           | 园区         | YQ02     | 公共区域文明行为管理 | YQ0205   | 宠物伤人       |  |\n| YQ           | 园区         | YQ02     | 公共区域文明行为管理 | YQ0206   | 违规停放       |  |\n| YQ           | 园区         | YQ02     | 公共区域文明行为管理 | YQ0207   | 占道经营       |  |\n| YQ           | 园区         | YQ02     | 公共区域文明行为管理 | YQ0208   | 油烟污染       |  |\n| YQ           | 园区         | YQ03     | 设施设备             | YQ0301   | 园区道路设施   |  |\n| YQ           | 园区         | YQ03     | 设施设备             | YQ0302   | 园区照明设备   |  |\n| YQ           | 园区         | YQ03     | 设施设备             | YQ0303   | 园区安防设备   |  |\n| YQ           | 园区         | YQ03     | 设施设备             | YQ0304   | 园区导视系统   |  |\n| YQ           | 园区         | YQ03     | 设施设备             | YQ0305   | 园区给排水系统 |  |\n| YQ           | 园区         | YQ03     | 设施设备             | YQ0306   | 休闲娱乐设施   |  |\n| YQ           | 园区         | YQ04     | 园区保洁             | YQ0401   | 清洁卫生       |  |\n| YQ           | 园区         | YQ04     | 园区保洁             | YQ0402   | 垃圾清运及分类 |  |\n| YQ           | 园区         | YQ04     | 园区保洁             | YQ0403   | 四害消杀       |  |\n| YQ           | 园区         | YQ05     | 园区绿化             | YQ0501   | 绿化景观配置   |  |\n| YQ           | 园区         | YQ05     | 园区绿化             | YQ0502   | 绿化养护       |  |\n| LY           | 楼宇         | LY01     | 楼栋卫生             | LY0101   | 清洁卫生       |  |\n| LY           | 楼宇         | LY01     | 楼栋卫生             | LY0102   | 卫生异味       |  |\n| LY           | 楼宇         | LY01     | 楼栋卫生             | LY0103   | 垃圾清运及回收 |  |\n| LY           | 楼宇         | LY01     | 楼栋卫生             | LY0104   | 四害消杀       |  |\n| LY           | 楼宇         | LY02     | 楼栋设备             | LY0201   | 灯光照明       |  |\n| LY           | 楼宇         | LY02     | 楼栋设备             | LY0202   | 门及门禁设备   |  |\n| LY           | 楼宇         | LY02     | 楼栋设备             | LY0203   | 电梯系统       |  |\n| LY           | 楼宇         | LY02     | 楼栋设备             | LY0204   | 消防安全       |  |\n| LY           | 楼宇         | LY02     | 楼栋设备             | LY0205   | 装饰装修       |  |\n| LY           | 楼宇         | LY03     | 不文明行为           | LY0301   | 乱摆放         |  |\n| LY           | 楼宇         | LY03     | 不文明行为           | LY0302   | 乱晾晒         |  |\n| LY           | 楼宇         | LY03     | 不文明行为           | LY0303   | 违规侵占       |  |\n| LY           | 楼宇         | LY03     | 不文明行为           | LY0304   | 非机动车进电梯 |  |\n| LY           | 楼宇         | LY03     | 不文明行为           | LY0305   | 其它           |  |\n| LY           | 楼宇         | LY04     | 公示栏               | LY0401   | 消息通知       |  |\n| LY           | 楼宇         | LY04     | 公示栏               | LY0402   | 电梯广告       |  |\n| DK           | 地库         | DK01     | 车辆出行             | DK0101   | 车辆识别       |  |\n| DK           | 地库         | DK01     | 车辆出行             | DK0102   | 道闸设备       |  |\n| DK           | 地库         | DK01     | 车辆出行             | DK0103   | 停车收费       |  |\n| DK           | 地库         | DK02     | 停车管理             | DK0201   | 乱停车、占车位 | 解决车位被占用、车辆行使道路、消防通道被占、堵塞等问题       |\n| DK           | 地库         | DK02     | 停车管理             | DK0203   | 车辆事故       |  |\n| DK           | 地库         | DK03     | 车库维护             | DK0301   | 环境卫生       | 解决客户反馈的关于卫生相关的问题                         |\n| DK           | 地库         | DK03     | 车库维护             | DK0302   | 照明设备       | 解决客户反馈的地库灯坏、灯不亮以及灯光暗等跟照明设备有关的问题 |\n| DK           | 地库         | DK03     | 车库维护             | DK0303   | 地库路面       |  |\n| HN           | 户内         | HN01     | 维修与安装           | HN0101   | 水电气         |  |\n| HN           | 户内         | HN01     | 维修与安装           | HN0102   | 防水漏水       |  |\n| HN           | 户内         | HN01     | 维修与安装           | HN0103   | 管道疏通       |  |\n| HN           | 户内         | HN01     | 维修与安装           | HN0104   | 五金配件       | 解决业主户内五金配件维修或者更换的问题                       |\n| HN           | 户内         | HN01     | 维修与安装           | HN0105   | 家电安装维修   |  |\n| HN           | 户内         | HN01     | 维修与安装           | HN0106   | 家具安装维修   |  |\n| HN           | 户内         | HN01     | 维修与安装           | HN0107   | 智能化设备维修 |  |\n| HN           | 户内         | HN01     | 维修与安装           | HN0108   | 宽带网络       |  |\n| HN           | 户内         | HN01     | 维修与安装           | HN0109   | 灯具维修       |  |\n| HN           | 户内         | HN01     | 维修与安装           | HN0110   | 土建装饰       |  |\n| HN           | 户内         | HN02     | 邻里协调             | HN0201   | 邻里纠纷       |  |\n| HN           | 户内         | HN02     | 邻里协调             | HN0202   | 邻里噪音       |  |\n| HN           | 户内         | HN02     | 邻里协调             | HN0203   | 邻里交流       |  |\n| HN           | 户内         | HN03     | 装修管理             | HN0301   | 装修咨询       |  |\n| HN           | 户内         | HN03     | 装修管理             | HN0302   | 装修办理       |  |\n| HN           | 户内         | HN03     | 装修管理             | HN0303   | 装修违规       |  |\n| HN           | 户内         | HN03     | 装修管理             | HN0304   | 装修噪音       |  |\n| HN           | 户内         | HN04     | 代办服务             | HN0401   | 代收代取快递   |  |\n| HN           | 户内         | HN04     | 代办服务             | HN0402   | 家政服务       |  |\n| HN           | 户内         | HN04     | 代办服务             | HN0403   | 房屋租赁       |  |\n| HN           | 户内         | HN04     | 代办服务             | HN0404   | 代客服务       |  |\n| HN           | 户内         | HN04     | 代办服务             | HN0405   | 物品借用       |  |\n| HN           | 户内         | HN05     | 咨询                 | HN0501   | 催单           |  |\n| HN           | 户内         | HN05     | 咨询                 | HN0502   | 咨询进展       |  |\n| HN           | 户内         | HN05     | 咨询                 | HN0503   | 硬件设施       |  |\n| HN           | 户内         | HN05     | 咨询                 | HN0504   | 交付服务       |  |\n| HN           | 户内         | HN06     | 费用类               | HN0601   | 物业费及开发票 |  |\n| HN           | 户内         | HN06     | 费用类               | HN0602   | 其他费用       |  |\n| ZH           | 综合         | ZH01     | 投诉                 | ZH0101   | 服务人员态度   |  |\n| ZH           | 综合         | ZH01     | 投诉                 | ZH0102   | 服务及时性     |  |\n| ZH           | 综合         | ZH01     | 投诉                 | ZH0103   | 服务效果       |  |\n| ZH           | 综合         | ZH01     | 投诉                 | ZH0104   | 服务管控       |  |\n| ZH           | 综合         | ZH02     | 表扬                 | ZH0201   | 表扬           |  |\n| ZH           | 综合         | ZH03     | 社区活动             | ZH0301   | 活动报名       |  |\n| ZH           | 综合         | ZH03     | 社区活动             | ZH0302   | 活动咨询       |  |\n| ZH           | 综合         | ZH03     | 社区活动             | ZH0303   | 活动评价       |  |\n| ZH           | 综合         | ZH04     | 车位办理             | ZH0401   | 车位咨询       |  |\n| ZH           | 综合         | ZH04     | 车位办理             | ZH0402   | 车位租赁       |  |\n| ZH           | 综合         | ZH04     | 车位办理             | ZH0403   | 车牌录入及更换 |  |\n| ZH           | 综合         | ZH05     | 建议                 | ZH0501   | 建议           |  |\n");
         messages.add(systemMessage);

        String userMessage = "你好,我在园区看到很多流浪猫.";
        UserMessage user = new UserMessage("user", userMessage);
        messages.add(user);
        Response<AiMessage> generate = model.generate(messages,toolSpecifications);
        AiMessage content = generate.content();
        messages.add(content);
        boolean updated = content.hasToolExecutionRequests();
        if(updated){
            List<ToolExecutionRequest> toolExecutionRequests = content.toolExecutionRequests();
            for (ToolExecutionRequest toolExecutionRequest : toolExecutionRequests) {
                DefaultToolExecutor defaultToolExecutor = toolExecutorMap.get(toolExecutionRequest.name());
                if(defaultToolExecutor==null){
                    continue;
                }
                String result = defaultToolExecutor.execute(toolExecutionRequest, 1);
                System.out.println("-------------------------------------------------------");
                System.out.println(toolExecutionRequest.name()+" "+toolExecutionRequest.arguments()+" result:"+result);
                ToolExecutionResultMessage toolExecutionResultMessage = ToolExecutionResultMessage.from(toolExecutionRequest, result);
                messages.add(toolExecutionResultMessage);
            }
            System.out.println("-------------------------------------------------------");
            StreamingChatLanguageModel model2 = OpenAiStreamingChatModel.builder()
                    .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                    .baseUrl("https://zen-vip.zeabur.app/v1")
                    .modelName("gpt-4o-2024-05-13")
                    .build();

            model2.generate(messages, new StreamingResponseHandler<AiMessage>() {
                @Override
                public void onNext(String token) {
                    System.out.println("onNext: " + token);
                }
                @Override
                public void onComplete(Response<AiMessage> response) {
                    System.out.println("onComplete: " + response);
                }
                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                }
            });
            System.out.println("-------------------------------------------------------");
            System.out.println(JSONObject.toJSONString(messages));
            //AiMessage finalResponse = model.generate(messages).content();
            //System.out.println(finalResponse.text());
        }else{
            System.out.println(content.text());
        }
        try {
            Thread.sleep(10000);
        }catch (Exception e){

        }
    }




    public static void wuyew(@MemoryId String firstLevelClassification) {

        InterviewFunction calculator = new InterviewFunction();
        Method[] methods = calculator.getClass().getMethods();

        Map<String,DefaultToolExecutor> toolExecutorMap = new HashMap<>();
        List<ToolSpecification> toolSpecifications = new ArrayList<>();
        for (Method method : methods) {
            if(method.isAnnotationPresent(Tool.class)){
                Tool annotation = method.getAnnotation(Tool.class);
                toolSpecifications.add(ToolSpecifications.toolSpecificationFrom(method));
                DefaultToolExecutor defaultToolExecutor = new DefaultToolExecutor(calculator, method);
                toolExecutorMap.put(annotation.name(),defaultToolExecutor);
            }
        }

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                .baseUrl("https://vip.zen-ai.top/v1")
                .modelName("gpt-4o")
                .build();

        List<ChatMessage> messages = new ArrayList<>();


        SystemMessage systemMessage = new SystemMessage("核心角色设定\n" +
                "你是一位专业的人力资源AI助手，负责在招聘平台以专业且友好的方式吸引优质候选人。你的任务是模拟资深HR的沟通方式与候选人互动。\n" +
                "当识别用户有面试意图当时候,请调用 setStatus 函数。" +
                "企业基础信息\n" +
                "[公司名称]：陕西会飞的猫网络科技所（个人独资企业）\n" +
                "[行业领域]：互联网/IT\n" +
                "[成立时间]：2023年\n" +
                "[公司规模]：0～50人\n" +
                "[总部地点]：陕西省西安市未央区明光路阳光北郡\n" +
                "招聘职位\n" +
                "[职位名称]：数据标注/AI训练师\n" +
                "[工作地点]：陕西省西安市未央区明光路阳光北郡4号楼\n" +
                "[工作时间]:   9:00到12点 13点到17点，周末双休。\n" +
                "[最短工作时间要求]:  3个月。\n" +
                "[职位类型]：实习\n" +
                "[薪资范围]：50-100元/天\n" +
                "[招聘人数]：5人\n" +
                "[紧急程度]：常规招聘\n" +
                "职位要求\n" +
                "【硬性要求】\n" +
                "- 学历要求：本科\n" +
                "- 工作经验：无\n" +
                "- 专业技能：标注方法优化、数据标注、标注数据分析、标注结果抽查、标注结果整理、文本标注\n" +
                "- 其他要求：无\n" +
                "面试信息\n" +
                "- 面试方式：线下\n" +
                "- 面试地址：陕西省西安市未央区明光路阳光北郡4号楼\n" +
                "\n" +
                "其他招聘信息\n" +
                "\n" +
                "对话规则\n" +
                "1. 内容生成原则：\n" +
                "  - 语气风格：专业严谨\n" +
                "  - 关键词密度：自然植入行业关键词\n" +
                "  - 转化引导：包含明确的CTA（立即申请/咨询详情）\n" +
                "  - 模拟人类：不要暴露你是AI助手，尽量模仿人类消息的风格。\n" +
                "  - 模糊不清楚的信息：不要虚构不存在的信息，如果用户问的问题你不清楚，就含糊过去。\n" +
                "  - 纯文本内容：不要使用markdown语法，招聘平台只支持纯文本内容。\n" +
                "  - 无关性原则：拒绝回复与招聘无关的内容\n" +
                "2. 合规性要求：\n" +
                "  - 自动规避年龄/性别/地域等敏感信息\n" +
                "  - 符合当地劳动法规定\n" +
                "  - 体现DEI（多元化、公平、包容）原则\n" +
                "候选人信息\n" +
                "- 候选人姓名：岳铜军\n" +
                "候选人沟通脚本\n" +
                "1. 确认是否感兴趣\n" +
                "2. 引导进入面试流程");

        messages.add(systemMessage);

        String userMessage = "你好,对你们公司非常感兴趣,我接受你们的面试";
        UserMessage user = new UserMessage("user", userMessage);
        messages.add(user);
        Response<AiMessage> generate = model.generate(messages,toolSpecifications);
        AiMessage content = generate.content();
        messages.add(content);
        boolean updated = content.hasToolExecutionRequests();
        if(updated){
            List<ToolExecutionRequest> toolExecutionRequests = content.toolExecutionRequests();
            for (ToolExecutionRequest toolExecutionRequest : toolExecutionRequests) {
                DefaultToolExecutor defaultToolExecutor = toolExecutorMap.get(toolExecutionRequest.name());
                if(defaultToolExecutor==null){
                    continue;
                }
                String result = defaultToolExecutor.execute(toolExecutionRequest, 1);
                System.out.println("-------------------------------------------------------");
                System.out.println(toolExecutionRequest.name()+" "+toolExecutionRequest.arguments()+" result:"+result);
                ToolExecutionResultMessage toolExecutionResultMessage = ToolExecutionResultMessage.from(toolExecutionRequest, result);
                messages.add(toolExecutionResultMessage);
            }
            System.out.println("-------------------------------------------------------");
            OpenAiChatModel model2 = OpenAiChatModel.builder()
                    .apiKey("sk-ZfLhlVjKuhKdZ8qY33C1A655C7104840A9Cc175250C05752")
                    .baseUrl("https://vip.zen-ai.top/v1")
                    .modelName("gpt-4o")
                    .build();
            Response<AiMessage> generate1 = model2.generate(messages);
            System.out.println("-------------------------------------------------------");
            System.out.println(generate1.content().text());
        }else{
            System.out.println(content.text());
        }
    }

}
