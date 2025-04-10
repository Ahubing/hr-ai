package com.open.ai.eros.permission.bean.vo;

import com.open.ai.eros.db.mysql.permission.entity.RolePermissionVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @类名：RolePermissionResult
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/13 23:02
 */

@NoArgsConstructor
@AllArgsConstructor
@ApiModel("权限结果类")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
public class RolePermissionResultVo {


    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("权限列表")
    private List<RolePermissionVo> permissionVos;

}
