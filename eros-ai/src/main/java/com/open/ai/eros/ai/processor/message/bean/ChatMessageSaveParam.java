package com.open.ai.eros.ai.processor.message.bean;

import com.open.ai.eros.ai.bean.req.AddChatMessageReq;
import com.open.ai.eros.common.vo.CacheUserInfoVo;
import com.open.ai.eros.db.mysql.ai.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @类名：ChatMessageSaveParam
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/15 21:52
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatMessageSaveParam {

    private CacheUserInfoVo cacheUserInfo;

    private ChatMessage chatMessage;

    private AddChatMessageReq req;

}
