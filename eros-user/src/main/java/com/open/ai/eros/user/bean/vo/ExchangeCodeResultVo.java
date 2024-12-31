package com.open.ai.eros.user.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @类名：ExchangeCodeResultVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/10 17:56
 */

@ApiModel("兑换码类")
@Data
public class ExchangeCodeResultVo {

    @ApiModelProperty("是否为最后一页")
    private boolean lastPage;

    @ApiModelProperty("兑换码列表")
    private List<ExchangeCodeVo> exchangeCodeVos;
}
