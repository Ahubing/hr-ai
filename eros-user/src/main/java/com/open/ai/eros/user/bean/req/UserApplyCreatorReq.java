package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 角色申请表，存储用户的角色申请信息
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */
@ApiModel("申请创作者控制类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserApplyCreatorReq implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * 额外说明
     */
    private String extra;

    /**
     * 联系访问
     */
    private String concat;


}
