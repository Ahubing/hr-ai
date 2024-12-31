package com.open.ai.eros.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
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
@ApiModel("修改渠道类")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModelConfigUpdateReq {


    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 访问地址
     */
    @ApiModelProperty("访问地址")
    private String baseUrl;

    /**
     * 访问token
     * token字段加密
     */
    @ApiModelProperty("token")
    private String token;

    /**
     * 模板区分
     */
    @ApiModelProperty("模版")
    private List<String> templateModel;

    /**
     * 倍率
     */
    @Min(0)
    @ApiModelProperty("倍率")
    @NotNull(message = "倍率不能为空")
    private Double multiple;

    /**
     * 渠道名称
     */
    @ApiModelProperty("渠道名")
    private String name;


    /**
     * 调用权重
     */
    @ApiModelProperty("权重")
    @NotNull(message = "id不能为空")
    private Integer weight;

    /**
     * 状态 0 ：管理员手动禁用 1：开启  2: 等待系统自动开启渠道 3：系统自动置为失效
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 扩展字段
     */
    @ApiModelProperty("额外说明")
    private String extra;


}
