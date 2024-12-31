package com.open.ai.eros.ai.model.processor;

import com.alibaba.fastjson.JSONObject;
import com.open.ai.eros.ai.model.bean.vo.CatchOpenAiError;
import com.open.ai.eros.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;


@Slf4j
public abstract class BaseModelProcessor {


    public void checkException(Response response) throws IOException {
        String msg;
        if (!response.isSuccessful()) {
            ResponseBody errorBody = response.body();
            if (errorBody == null) {
                throw new BizException("code is error,body is null");
            } else {
                msg = errorBody.string();
                log.error("checkException error errorBodyString={}",msg);
                if(StringUtils.isNoneEmpty(msg)){
                    CatchOpenAiError error = JSONObject.parseObject(msg, CatchOpenAiError.class);
                    if(error!=null && error.error!=null){
                        throw new BizException(JSONObject.toJSONString(error));
                    }
                }
            }
            throw new BizException(msg);
        }
    }
}
