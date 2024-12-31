package com.open.ai.eros.creator.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @类名：MaskSearchResultVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/14 21:43
 */

@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("面具的搜索结果类")
@Data
public class MaskSearchResultVo {


    @ApiModelProperty("是否尾页")
    private boolean lastPage;

    @ApiModelProperty("面具集合")
    private List<CMaskVo> maskVos;

}
