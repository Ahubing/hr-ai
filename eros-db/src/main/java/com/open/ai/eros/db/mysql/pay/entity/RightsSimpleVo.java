package com.open.ai.eros.db.mysql.pay.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @类名：RightsSimpleVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/26 17:09
 */
@Data
public class RightsSimpleVo {

    /**
     * 权益id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 权益名称
     */
    private String name;


}
