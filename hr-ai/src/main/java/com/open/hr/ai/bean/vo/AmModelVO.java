package com.open.hr.ai.bean.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @类名：ModelVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/25 16:12
 */

@ApiModel("模型列表")
@Data
public class AmModelVO {

    private Long id;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型值
     */
    private String value;

    /**
     * 模型描述
     */
    private String description;

    /**
     * temperature参数
     */
    private Double temperature;

    /**
     * top_p参数
     */
    private Double topP;

    /**
     * 状态: 1-启用, 0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
