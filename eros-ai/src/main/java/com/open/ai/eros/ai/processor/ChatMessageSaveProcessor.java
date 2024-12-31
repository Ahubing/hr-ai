package com.open.ai.eros.ai.processor;

import com.open.ai.eros.ai.processor.message.bean.ChatMessageSaveParam;
import com.open.ai.eros.common.vo.ResultVO;

import javax.servlet.http.HttpServletResponse;

public interface ChatMessageSaveProcessor {




    ResultVO after(ChatMessageSaveParam param, HttpServletResponse response);



}
