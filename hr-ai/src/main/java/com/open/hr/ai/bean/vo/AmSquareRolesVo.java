package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 模型训练广场角色
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmSquareRolesVo implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建者id
     */
    private Integer adminId;

    /**
     * 行业
     */
    private String profession;

    /**
     * 关键词，英文逗号分割
     */
    private String keywords;

    /**
     * 使用次数
     */
    private Integer useNums;

    /**
     * 效率提升，如：50；表示50%
     */
    private BigDecimal efficiencyUp;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
