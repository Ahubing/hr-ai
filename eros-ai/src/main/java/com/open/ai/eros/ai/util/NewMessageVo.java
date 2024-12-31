package com.open.ai.eros.ai.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @类名：NewMessageVO
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/10/22 23:18
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewMessageVo {

    private Long chatId;

    private String text;

    private Long replyId;

}
