package com.open.hr.ai.bean;

import com.open.ai.eros.db.mysql.hr.entity.AmZpLocalAccouts;
import com.open.ai.eros.db.mysql.hr.entity.AmZpPlatforms;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel("平台列表列表结果类")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@Builder
public class AmZpPlatformsResultVo {

    @ApiModelProperty("平台列表")
    private List<AmZpPlatforms> platforms;


}
