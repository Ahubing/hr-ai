package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@ApiModel("删除招聘平台参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmZpAccountDelReq implements Serializable {

    @ApiModelProperty(value = "招聘账号id", required = true, notes = "招聘账号id不能为空")
    @NotEmpty(message = "招聘账号id不能为空")
    private String id;


}
