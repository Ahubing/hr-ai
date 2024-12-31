package com.open.ai.eros.ai.model.data;

import com.open.ai.eros.ai.bean.dto.AITextRequest;
import com.open.ai.eros.ai.bean.dto.AITextResponse;
import com.open.ai.eros.common.vo.ResultVO;

public interface DataAfterProcessor {





    /**
     * AI之后的处理
     * @param request
     * @param response
     * @return
     */
    ResultVO after(AITextRequest request, AITextResponse response);


}
