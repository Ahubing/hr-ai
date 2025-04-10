package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@ApiModel("添加平台参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmZpPlatformAddReq implements Serializable {

    @ApiModelProperty("平台名称")
    @NotEmpty(message = "平台名称不能为空")
    private String name;

}
