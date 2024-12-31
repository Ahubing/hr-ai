package com.open.ai.eros.user.bean.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@ApiModel("简单用户信息")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SimpleUserVo {

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("昵称")
    private String userName;


    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("用户id")
    private Long id;

}
