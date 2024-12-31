package com.open.ai.eros.ai.bean.vo;

import lombok.Data;

/**
 * @Author liuzilin
 * @Date 2023/11/5 14:03
 */
@Data
public class ResponseVo {
    private String message;
    private Integer code;
    private Object data;
}
