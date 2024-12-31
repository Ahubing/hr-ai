package com.open.ai.eros.ai.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @类名：ModelVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/25 16:12
 */

@ApiModel("模型分组列表")
@Data
public class ModelGroupVo {

    @ApiModelProperty("渠道模版")
    private String template;

    @ApiModelProperty("说明")
    private String desc;

    @ApiModelProperty("支持的模型")
    private List<String> models;
}
