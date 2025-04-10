package com.open.ai.eros.ai.model.bean.vo;

import com.open.ai.eros.db.mysql.ai.entity.ModelConfigVo;
import com.open.ai.eros.common.vo.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResultVo {

    /**
     * 聊天记录
     */
    private ChatMessage chatMessage;
    private long promptTokenNumber;// 提示词消耗的token 数
    private long relyTokenNumber; // 返回内容消耗的token数
    private String model; //模型接口
    // 渠道信息
    private ModelConfigVo modelConfigVo;
    private Long firstRelyTime;//第一次回答时间
    private Long costTime;//总耗费时间
    private String errorMessage;//错误信息
    // 本次计费
    private long cost;

}
