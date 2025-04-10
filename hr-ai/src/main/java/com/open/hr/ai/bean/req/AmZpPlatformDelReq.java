package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel("删除招聘平台参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmZpPlatformDelReq implements Serializable {

    @ApiModelProperty("招聘平台id")
    @NotNull(message = "招聘id不能为空")
    private Long id;


}
