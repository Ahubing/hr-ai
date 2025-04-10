package com.open.ai.eros.ai.model.data.before;

import com.open.ai.eros.ai.bean.dto.AITextRequest;
import com.open.ai.eros.ai.bean.dto.AITextResponse;
import com.open.ai.eros.ai.model.data.DataAfterProcessor;
import com.open.ai.eros.common.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @类名：AIToolBeforeProcessor
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/12/9 15:42
 */
@Component
@Slf4j
public class AIToolDataBeforeProcessor implements DataAfterProcessor {




    @Override
    public ResultVO after(AITextRequest request, AITextResponse response) {
        return ResultVO.success();
    }




}
