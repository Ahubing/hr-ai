package com.open.ai.eros.user.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @类名：UserExchangeCodeRecordVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/28 23:58
 */

@ApiModel("用户的兑换码记录")
@Data
public class UserExchangeCodeRecordVo {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("用户id")
    private Long id;


    @ApiModelProperty("头像")
    private String avatar;


    @ApiModelProperty("昵称")
    private String userName;


    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;


}
