package com.open.ai.eros.ai.bean.req;

import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.creator.bean.vo.BMaskVo;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;

/**
 * @类名：TestMaskReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/17 22:02
 */

/**
 * 创作者调试面具的入参
 *
 */
@Data
public class TestMaskReq {

    @NotNull(message = "面具不能为空")
    private BMaskVo maskVo;


    private String templateModel;

    /**
     * 用户的问题
     */
    @NotEmpty(message = "用户的问题不能为空")
    private String userPrompt;

    /**
     * 上下文
     */
    private LinkedList<ChatMessage> messages;

}
