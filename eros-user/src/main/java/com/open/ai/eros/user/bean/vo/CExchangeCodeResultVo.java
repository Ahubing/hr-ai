package com.open.ai.eros.user.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @类名：CExchangeCodeResultVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/13 1:55
 */

@ApiModel("兑换码结果类")
@Data
public class CExchangeCodeResultVo {


    @ApiModelProperty("是否为最后一页")
    private boolean lastPage;

    private List<UserExchangeCodeRecordVo> userExchangeCodeRecords;

}
