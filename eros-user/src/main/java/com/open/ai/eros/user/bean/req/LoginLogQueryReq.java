package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 登录日志---查询请求参数类
 */
@Data
public class LoginLogQueryReq {

    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小为1")
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNum;

    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 50, message = "每页条数最大为50")
    @ApiModelProperty(value = "每页条数", required = true)
    private Integer pageSize;

}
