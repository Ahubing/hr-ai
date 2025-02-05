package com.open.hr.ai.bean.req;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.common.vo.ChatMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author 
 * @Date 2025/1/7 20:54
 */
@Data
public class ClientBossNewMessageReq {

    /**
     * 与python定义的字段一致
     * user_id
     */
    @ApiModelProperty(value = "user_id")
    private String user_id;

    /**
     * 与python定义的字段一致
     * recruiter_id 雇佣者id
     */
    @ApiModelProperty(value = "recruiter_id")
    private String recruiter_id;


    /**
     * 与python定义的字段一致
     * messages
     */
    @ApiModelProperty(value = "messages")
    private List<ChatMessage> messages;

    @ApiModelProperty(value = "chat_info")
    private JSONObject chat_info;


    @ApiModelProperty(value = "attachment_resume")
    private List<String> attachmentResume;


}
