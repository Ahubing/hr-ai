package com.open.ai.eros.user.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @类名：UserAIConsumeRecordQueryResultVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/18 13:14
 */
@ApiModel("用户消费记录的结果类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserAIConsumeRecordQueryResultVo {

    @ApiModelProperty("是否是最后一页")
    private boolean lastPage = true;


    @ApiModelProperty("记录")
    private List<UserAiConsumeRecordVo> recordVos;

}
