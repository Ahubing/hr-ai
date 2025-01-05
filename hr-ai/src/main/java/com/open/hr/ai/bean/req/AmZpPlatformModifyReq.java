package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@ApiModel("修改平台名称参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmZpPlatformModifyReq implements Serializable {

    @ApiModelProperty("平台id")
    @NotEmpty(message = "平台id不能为空")
    private Long id;

    @ApiModelProperty("平台名称")
    @NotEmpty(message = "平台名称不能为空")
    private String name;


}
