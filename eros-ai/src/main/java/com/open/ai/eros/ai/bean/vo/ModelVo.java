package com.open.ai.eros.ai.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @类名：ModelVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/25 16:12
 */

@ApiModel("模型列表")
@Data
public class ModelVo {

    @ApiModelProperty("模型名称")
    private String name;

    @ApiModelProperty("template:model")
    private String value;

}
