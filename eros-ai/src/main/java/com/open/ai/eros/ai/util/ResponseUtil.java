package com.open.ai.eros.ai.util;

import com.alibaba.fastjson2.JSON;
import com.open.ai.eros.ai.bean.vo.ErrorMessageResultVo;
import com.open.ai.eros.ai.bean.vo.ResponseVo;
import com.open.ai.eros.common.constants.BaseCodeEnum;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author 
 * @Date 2023/11/5 14:06
 */
@Component
public class ResponseUtil {

    public static String getErrorVo(String errorMsg,Integer rcode){
        ResponseVo responseVo = new ResponseVo();
        responseVo.setMessage(errorMsg);
        responseVo.setCode(rcode);
        return String.format("data: %s\n\n", JSON.toJSONString(responseVo));
    }

    public static void errorMsg(String errorMsg, Integer rcode, HttpServletResponse response) throws IOException {
        response.setStatus(rcode);
        response.setContentType("application/json");
        ErrorMessageResultVo messageResultVo = ErrorMessageResultVo.builder()
                .error(
                        new ErrorMessageResultVo.ErrorMessage(errorMsg,String.valueOf(rcode))
                ).build();
        response.getOutputStream().write(JSON.toJSONString(messageResultVo).getBytes());
    }

    public static String getDrawResponseVo(Object data, BaseCodeEnum baseCodeEnum){
        ResponseVo responseVo = new ResponseVo();
        responseVo.setMessage(baseCodeEnum.getMsg());
        responseVo.setCode(baseCodeEnum.getCode());
        responseVo.setData(data);
        return JSON.toJSONString(responseVo);
    }

}
