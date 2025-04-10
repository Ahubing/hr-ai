package com.open.ai.eros.pay.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户名下面具数据
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-12
 */
@ApiModel("用户名下面具数据")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderStatListSearchVo implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * 查询时间窗口 -1 查询全部, 1 本周, 2 这个月
     */
    private Integer timeWindow = -1;
    /**
     * 页码
     */
    private Integer page = 1;
    /**
     * 页数
     */
    private Integer pageSize = 10;




}
