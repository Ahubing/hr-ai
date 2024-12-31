package com.open.ai.eros.pay.goods.bean.req;

import com.open.ai.eros.pay.goods.bean.vo.RightsRuleVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 权益
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RightsUpdateReq implements Serializable {

    private static final long serialVersionUID=1L;

    @NotNull(message = "id不能为空")
      private Long id;

    /**
     * 权益名称
     */
    @NotEmpty(message = "权益名称不能为空")
    private String name;

    /**
     * 权益的具体行为
     */
    @NotEmpty(message = "权益不为空")
    private String rightsValue;

    /**
     * 说明
     */
    @NotEmpty(message = "说明不为空")
    private String intro;


    /**
     * 1:上架 2：下架
     */
    @NotNull(message = "状态不能为空")
    private Integer status;


    /**
     * 有效时间 -1 为永久 单位 小时
     */
    @NotNull(message = "有效时间不能为空")
    private Long effectiveTime;


    /**
     * 类型  @see com.open.ai.eros.db.constants.RightsTypeEnum
     */
    @NotNull(message = "权益类型不能为空")
    private String type;


    /**
     * 可以使用的模型
     */
    @NotNull(message = "可以使用的模型不能为空")
    private String canUseModel;


    /**
     * 更新的规则
     */
    private RightsRuleVo rule;




}
