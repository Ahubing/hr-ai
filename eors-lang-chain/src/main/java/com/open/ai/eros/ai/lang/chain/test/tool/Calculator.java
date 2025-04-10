package com.open.ai.eros.ai.lang.chain.test.tool;

import com.open.ai.eros.common.util.DateUtils;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

/**
 * @类名：Calculator
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/9 20:02
 */
public class Calculator {

    @Tool(name = "add")
    public double add(AddNumber addNumber) {
        return addNumber.number + addNumber.getA();
    }

    @Tool(name = "squareRoot")
    public double squareRoot(double x) {
        return Math.sqrt(x);
    }


    @Tool(name = "nowTime",value = {"当前时间"})
    public String nowTime(){
        return DateUtils.formatDate(new Date(),DateUtils.FORMAT_YYYY_MM_DD_HHMMSS);
    }


    @Tool(name = "addTaskEvent",value = {"添加时间调度任务"})
    public void addTaskEvent(@P("任务事件说明") String event, @P("任务触发时间，格式: yyyy-MM-dd HH:mm:ss") String time){
        System.out.println(event+"  "+time);
    }



    public static void main(String[] args) {

        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-R2rCl3JhjqlOl2ILE4676a8b502f42E0B4896810A909Dc75")
                .baseUrl("https://api.open-proxy.cn/v1")
                .modelName("gpt-4o-2024-05-13")
                .build();

        Calculator calculator = new Calculator();

        //Function<Object, String> systemMessageProvider = (memoryId) -> {
        //    if (memoryId.equals("1")) {
        //        return "You are a helpful assistant. The user prefers to be called 'Your Majesty'.";
        //    } else {
        //        return "You are a helpful assistant.";
        //    }
        //};

        PersistentChatMemoryStore store = new PersistentChatMemoryStore();

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(store)
                .build();

        MathGenius mathGenius = AiServices.builder(MathGenius.class)
                .tools(calculator)
                .chatLanguageModel(model)
                .chatMemoryProvider(chatMemoryProvider)
                .build();

        String answer = mathGenius.ask(1,"现在几点了");
        System.out.println("ai回答-------------"+answer);

        //List<ChatMessage> messages = new ArrayList<>();
        //String userMessage = "输出明天下午两点的时间格式? 和 1加2的和是多少";
        //messages.add(new UserMessage("user",userMessage));
        //List<ToolSpecification> toolSpecifications = ToolSpecifications.toolSpecificationsFrom(Calculator.class);
        //
        //UserMessage userMessage = UserMessage.from("输出明天下午两点的时间格式? 和 1加2的和是多少");
        //
        //Response<AiMessage> response = model.generate(Arrays.asList(userMessage), toolSpecifications);
        //AiMessage aiMessage = response.content();
    }



    static class PersistentChatMemoryStore implements ChatMemoryStore {

        private final Map<Integer, String> map = new ConcurrentHashMap<>();

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
            String json = map.get((int) memoryId);
            return messagesFromJson(json);
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            String json = messagesToJson(messages);
            map.put((int) memoryId, json);
        }

        @Override
        public void deleteMessages(Object memoryId) {
            map.remove((int) memoryId);
        }
    }

}
