package com.open.hr.ai.bean;

import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel("多账号登录列表结果类")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
public class AmZpAccoutsResultVo {
    @ApiModelProperty("账号数")
    private int account_num;
    @ApiModelProperty("在线账号数")
    private int account_online_num;
    @ApiModelProperty("运行账号数")
    private int account_running_num;
    @ApiModelProperty("账号列表")
    private List<AmZpLocalAccouts> list;
    @ApiModelProperty("平台列表")
    private List<AmZpPlatforms> platforms;
    @ApiModelProperty("城市列表-固定值")
    private String[] citys;

}
