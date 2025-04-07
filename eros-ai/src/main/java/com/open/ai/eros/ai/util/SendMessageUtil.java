package com.open.ai.eros.ai.util;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.req.IcSpareTimeReq;
import com.open.ai.eros.ai.bean.vo.IcSpareTimeVo;
import com.open.ai.eros.ai.bean.vo.MaskSseConversationVo;
import com.open.ai.eros.ai.manager.ICAiManager;
import com.open.ai.eros.common.constants.ClientTaskTypeEnums;
import com.open.ai.eros.db.constants.AIRoleEnum;
import com.open.ai.eros.db.mysql.ai.service.IChatMessageService;
import com.open.ai.eros.db.mysql.hr.entity.*;
import com.open.ai.eros.db.mysql.hr.service.IAmChatMessageService;
import com.open.ai.eros.db.mysql.hr.service.IAmClientTasksService;
import com.open.ai.eros.db.mysql.hr.service.IAmZpLocalAccoutsService;
import com.open.ai.eros.db.mysql.hr.service.impl.AmChatMessageServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmClientTasksServiceImpl;
import com.open.ai.eros.db.mysql.hr.service.impl.AmZpLocalAccoutsServiceImpl;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @类名：SendMessageUtil
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/4 17:00
 */
@Data
public class SendMessageUtil {

    /**
     data:{ChAT:EROS_END_OF_MESSAGE}
     data:{CHAT:EROS_START_OF_MESSAGE}

     data:{CHAT_ID:xxxx}
     data:{REPLY_ID:xxxxx}
     */

    public static final String REPLY_ID = "data:{\"REPLY_ID\":\"%s\"}";

    public static final String CHAT_ID = "data:{\"CHAT_ID\":\"%s\"}";

    public static final String START_FLAG = "data:{\"CHAT\":\"EROS_START_OF_MESSAGE\"}";

    public static final String END_FLAG = "data:{\"CHAT\":\"EROS_END_OF_MESSAGE\"}";


    public static final String EROS_CONNECT_SUCCESS = "data:{\"CONNECT\":\"EROS_CONNECT_SUCCESS\"}";


    public static final String HEART = "data:{\"CONNECT\": \"HEARTBEAT\"}";

    private static final Logger log = LoggerFactory.getLogger(SendMessageUtil.class);

    private static final IAmChatMessageService chatMessageService = SpringUtil.getBean(AmChatMessageServiceImpl.class);

    private static final IAmClientTasksService clientTasksService = SpringUtil.getBean(AmClientTasksServiceImpl.class);

    private static final ICAiManager icAiManager = SpringUtil.getBean(ICAiManager.class);


    /**
     * 新的对话消息
     */
    public static final String NEW_MESSAGE = "data:{\"newMessage\": %s}";


    public String formatAIStr(String msg,boolean IS_END){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text",msg);
        jsonObject.put("CHAT_ID",this.chatId);
        jsonObject.put("IS_END",IS_END);
        jsonObject.put("REPLY_ID",this.replyId);
        if(this.senderId!=null){
            jsonObject.put("SENDER_Id",this.senderId);
        }
        return "data:"+jsonObject.toJSONString();
    }


    private OutputStream userOs;

    private MaskSseConversationVo maskSseConversationVo;

    private HttpServletResponse response;

    private String source;

    private String chatId;

    private String replyId;

    /**
     * 来自的用户id
     */
    private String senderId;


    public SendMessageUtil() {
    }

    public SendMessageUtil(HttpServletResponse response) throws IOException {
        this.response = response;
        this.userOs = response.getOutputStream();
    }

    public SendMessageUtil(HttpServletResponse response,MaskSseConversationVo maskSseConversationVo) throws IOException {
        this.maskSseConversationVo = maskSseConversationVo;
        this.response = response;
        this.userOs = response.getOutputStream();
    }

    public void setMaskSseConversationVo(MaskSseConversationVo maskSseConversationVo) {
        this.maskSseConversationVo = maskSseConversationVo;
    }


    public void chatIdInfo(Long chatId,Long replyId,Long senderId){
        this.chatId = String.valueOf(chatId);
        this.replyId = String.valueOf(replyId);
        if(senderId!=null){
            this.senderId = String.valueOf(senderId);
        }
    }

    /**
     * 优先 sse发送消息
     *
     * @param message
     */
    public void sendMessage(String message){
        try {
            if(this.maskSseConversationVo!=null){
                SseEmitter sseEmitter = maskSseConversationVo.getSseEmitter();
                sseEmitter.send(message.getBytes(Charset.defaultCharset()), MediaType.TEXT_PLAIN);
                return;
            }
            if(this.userOs != null){
                if("chat".equals(source)){
                    if(message.contains("text\":")){
                        String replace = message.replace("data:", "");
                        JSONObject jsonObject = JSONObject.parseObject(replace);
                        String text = jsonObject.getString("text");
                        if(text!=null){
                            this.userOs.write( text.getBytes(Charset.defaultCharset()));
                            userOs.flush();
                        }
                    }
                    return;
                }
                this.userOs.write( (message).getBytes(Charset.defaultCharset()));
                userOs.flush();
            }
        }catch (Exception e){

        }
    }

    private static String generateInterviewCancelContent(LocalDateTime interviewTime){
        if(interviewTime == null){
            return "";
        }
        String cancelContent =
                "感谢您对我们的关注及面试准备。由于我方内部临时出现调整，我们不得不取消原定于[time]的面试安排，对此我们深表歉意。\n" +
                        "若您仍对该岗位感兴趣，我们将在后续招聘计划明确后优先与您联系。\n" +
                        "再次感谢您的理解与支持，祝您求职顺利！";
        return cancelContent.replace("[time]", interviewTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    private static String generateRefuseContent(){
        String cancelContent =
                "感谢您对我们的关注，但是您的简历与我们当前的职位要求并不完全匹配。祝您找到更合适的职位";
        return cancelContent;
    }

    private static String generateInterviewModifyContent(IcSpareTimeVo spareTimeVo, LocalDateTime interviewTime){
        String modifyContent =
                "感谢您对我们的关注及面试准备。由于招聘流程调整，我们希望与您协商调整原定于[time]的面试安排。\n" +
                        "以下为可协调的新时间段，请您确认是否方便：\n" +
                        " [newTime]\n" +
                        "若以上时间均不合适，请您提供方便的时间段，我们将尽力配合。\n" +
                        "如您需进一步沟通，请随时通过与我联系。对此次调整带来的不便，我们深表歉意，并感谢您的理解与配合！";
        StringBuilder newTimeStr = new StringBuilder();
        List<IcSpareTimeVo.SpareDateVo> spareDateVos = spareTimeVo.getSpareDateVos();
        for (IcSpareTimeVo.SpareDateVo spareDateVo : spareDateVos) {
            newTimeStr.append(spareDateVo.getLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("：");
            newTimeStr.append("   ").append(spareDateVo.getSparePeriodVos().stream().map(sparePeriodVo ->
                    sparePeriodVo.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "至" + sparePeriodVo.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))).collect(Collectors.joining("，"))).append("\n");
        }
        return modifyContent.replace("[newTime]",newTimeStr).replace("[time]", interviewTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

    }

    public static void generateAsyncMessage(AmResume resume, AmZpLocalAccouts account, IcRecord record, String type) {
        log.info("generateAsyncMessage type:{}",type);
        String content = "";
        switch (type){
            case "cancel":
                content = generateInterviewCancelContent(record.getStartTime());
                break;
            case "modify":
                IcSpareTimeVo spareTimeVo = icAiManager.getSpareTime(new IcSpareTimeReq(record.getMaskId(), LocalDateTime.now(), LocalDateTime.now().plusDays(7))).getData();
                content = generateInterviewModifyContent(spareTimeVo, record.getStartTime());
                break;
            case "refuse":
                content = generateRefuseContent();
                break;
        }
        log.info("generateAsyncMessage content:{}",content);
        saveAsyncMessage(resume, account, content);
    }


    private static void saveAsyncMessage(AmResume resume, AmZpLocalAccouts account, String content) {
        AmClientTasks amClientTasks = new AmClientTasks();
        JSONObject jsonObject = new JSONObject();
        JSONObject searchObject = new JSONObject();
        searchObject.put("encrypt_friend_id", resume.getEncryptGeekId());
        searchObject.put("name", resume.getName());
        jsonObject.put("is_system_message",true);
        jsonObject.put("user_id", resume.getUid());
        jsonObject.put("messages", Collections.singletonList(content));
        jsonObject.put("search_data", searchObject);

        amClientTasks.setTaskType(ClientTaskTypeEnums.SEND_MESSAGE.getType());
        amClientTasks.setOrderNumber(ClientTaskTypeEnums.SEND_MESSAGE.getOrder());
        amClientTasks.setSubType(ClientTaskTypeEnums.SEND_MESSAGE.getSubType());
        amClientTasks.setDetail(String.format("回复用户: %s , 回复内容为: %s", resume.getName(), content));

        amClientTasks.setBossId(resume.getAccountId());
        amClientTasks.setData(jsonObject.toJSONString());
        amClientTasks.setStatus(0);
        amClientTasks.setCreateTime(LocalDateTime.now());
        long startTime = System.currentTimeMillis();
        boolean result = clientTasksService.save(amClientTasks);
        long endTime = System.currentTimeMillis();
        log.info("clientTasksService.save:{}" ,endTime - startTime);
        startTime = endTime;

        log.info("生成消息任务处理结果 amClientTask={} result={}", JSONObject.toJSONString(amClientTasks), result);
        if (result) {
            // 生成聊天记录
            AmChatMessage amChatMessage = new AmChatMessage();
            amChatMessage.setConversationId(account.getId() + "_" + resume.getUid());
            amChatMessage.setUserId(Long.parseLong(account.getExtBossId()));
            amChatMessage.setRole(AIRoleEnum.ASSISTANT.getRoleName());
            amChatMessage.setType(-1);
            amChatMessage.setChatId(UUID.randomUUID().toString());
            amChatMessage.setContent(content);
            amChatMessage.setCreateTime(LocalDateTime.now());
            boolean save = chatMessageService.save(amChatMessage);
            endTime = System.currentTimeMillis();
            log.info("clientTasksService.save:{}" ,endTime - startTime);
            log.info("生成聊天记录结果 amChatMessage={} result={}", JSONObject.toJSONString(amChatMessage), save);
        }
    }

}
