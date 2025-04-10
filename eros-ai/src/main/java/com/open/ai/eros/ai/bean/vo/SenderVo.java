package com.open.ai.eros.ai.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @类名：SenderVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/11/6 17:01
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SenderVo {

    @ApiModelProperty("用户：user ai: assistant ")
    private String role;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("昵称")
    private String userName;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("发生者id")
    private Long id;

}
