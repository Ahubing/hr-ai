package com.open.ai.eros.ai.model.data;

import com.open.ai.eros.ai.bean.dto.AITextRequest;
import com.open.ai.eros.ai.bean.dto.AITextResponse;
import com.open.ai.eros.common.vo.ResultVO;

public interface DataBeforeProcessor {


    /**
     * AI之前的处理
     * @param request
     * @param response
     * @return
     */
    ResultVO before(AITextRequest request, AITextResponse response);




}
