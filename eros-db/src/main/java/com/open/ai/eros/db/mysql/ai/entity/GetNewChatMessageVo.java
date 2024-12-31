package com.open.ai.eros.db.mysql.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @类名：GetChatMessageVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/9 23:07
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetNewChatMessageVo {

    private String conversionId;

    private Long userId;


    private Integer pageSize;

    private Long chatId;

    /**
     * 分享的会话id
     */
    private String shareConversionId;

    /**
     * 聊天起始id
     */
    private Long startChatId;

    /**
     * 聊天结束id
     */
    private Long endChatId;

}
