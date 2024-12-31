package com.open.ai.eros.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 模型配置
 * </p>
 *
 * @author Administrator
 * @since 2023-09-09
 */
@ApiModel("新增渠道类")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModelConfigAddReq {


    /**
     * 访问地址
     */
    @ApiModelProperty("访问地址")
    @NotEmpty(message = "访问地址不能为空")
    private String baseUrl;

    /**
     * 访问token
     * token字段加密
     */
    @ApiModelProperty("token")
    @NotEmpty(message = "访问token不能为空")
    private String token;

    /**
     * 模板区分
     */
    @ApiModelProperty("模版")
    @NotEmpty(message = "模版不能为空")
    private List<String> templateModel;

    /**
     * 渠道名称
     */
    @ApiModelProperty("渠道名")
    @NotEmpty(message = "渠道名不能为空")
    private String name;


    /**
     * 倍率
     */
    @Min(0)
    @ApiModelProperty("倍率")
    @NotNull(message = "倍率不能为空")
    private Double multiple;

    /**
     * 调用权重
     */
    @ApiModelProperty("权重")
    @NotNull(message = "权重不能为空")
    private Integer weight;

    /**
     * 状态 0 ：管理员手动禁用 1：开启  2: 等待系统自动开启渠道 3：系统自动置为失效
     */
    @ApiModelProperty("状态")
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 扩展字段
     */
    @ApiModelProperty("额外说明")
    private String extra;


}
