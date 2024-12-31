package com.open.ai.eros.user.bean.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@ApiModel("申请创作者用户信息类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ApplyCreatorUserVo {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("申请id")
    private Long id;

    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("用户id")
    private Long userId;

    /**
     * 用户头像
     */
    @ApiModelProperty("用户头像")
    private String avatar;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;


    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String userName;


    /**
     * 额外说明
     */
    @ApiModelProperty("额外说明")
    private String extra;

    /**
     * 联系方式
     */
    @ApiModelProperty("联系方式")
    private String concat;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;


}
