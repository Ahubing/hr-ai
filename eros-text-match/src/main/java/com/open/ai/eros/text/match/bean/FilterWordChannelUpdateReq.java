package com.open.ai.eros.text.match.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-10-17
 */
@ApiModel("通道修改类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FilterWordChannelUpdateReq implements Serializable {


    @ApiModelProperty("ID")
    private Long id;

    /**
     * 通道类型 1：风险词，2：url 3 全拼  4：白词 5. 文本匹配
     */
    @ApiModelProperty("通道类型")
    private Integer type;

    /**
     * 通道名称
     */
    @ApiModelProperty("通道名称")
    private String channelName;

    @ApiModelProperty("备注说明")
    private String remark;

    /**
     * 状态 1：有效  0：无效
     */
    @ApiModelProperty("状态")
    private Integer status;


}
