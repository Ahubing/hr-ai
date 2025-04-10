package com.open.ai.eros.user.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @类名：UserBalanceRecordResultVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/9 2:26
 */
@ApiModel("余额记录表的结果类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserBalanceRecordResultVo {


    @ApiModelProperty("是否为最后一页")
    private boolean lastPage;

    @ApiModelProperty("余额记录")
    private List<UserBalanceRecordVo> recordVos;

}
