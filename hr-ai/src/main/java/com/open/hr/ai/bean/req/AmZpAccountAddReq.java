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
public class AmZpAccountAddReq implements Serializable {

    @ApiModelProperty("招聘平台id")
    @NotEmpty(message = "招聘id不能为空")
    private Long platformId;

    @ApiModelProperty("招聘账号")
    @NotEmpty(message = "招聘账号不能为空")
    private String account;

    @ApiModelProperty("招聘城市")
    @NotEmpty(message = "招聘城市不能为空")
    private String city;



}
