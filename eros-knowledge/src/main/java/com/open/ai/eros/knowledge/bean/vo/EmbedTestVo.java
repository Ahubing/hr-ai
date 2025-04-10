package com.open.ai.eros.knowledge.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @类名：EmbedTestVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/11 23:20
 */

@ApiModel("向量类")
@Data
public class EmbedTestVo {


    @ApiModelProperty("文本内容")
    @NotNull(message = "文本内容不能为空")
    private String text;



    @ApiModelProperty("模型模版")
    @NotNull(message = "模型模版不能为空")
    private String templateModel;


}
