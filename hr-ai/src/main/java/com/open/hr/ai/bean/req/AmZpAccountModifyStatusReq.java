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
public class AmZpAccountModifyStatusReq implements Serializable {

    @ApiModelProperty("招聘账号id")
    @NotEmpty(message = "招聘账号id不能为空")
    private String id;//数据库表结构是个string属性

    @ApiModelProperty("账号状态")
    @NotEmpty(message = "0运行中，1暂停")
    private int is_running;//0运行中，1暂停


}
