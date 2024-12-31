package com.open.ai.eros.ai.util;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.bean.vo.MaskSseConversationVo;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

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

}
