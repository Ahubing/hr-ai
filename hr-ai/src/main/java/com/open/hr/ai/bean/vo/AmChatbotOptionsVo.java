package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import com.open.ai.eros.db.mysql.hr.entity.AmChatbotOptionsItems;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmChatbotOptionsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 方案名
     */
    private String name;

    /**
     * 关联的职位ids,用英文逗号分割
     */
    private String positionIds;

    /**
     * 方案类型。0为boss从未回复，1为boss询问信息后
     */
    private Integer type;

    /**
     * 男士称呼别名
     */
    private String manAlias;

    /**
     * 女士称呼别名
     */
    private String womanAlias;

    /**
     * 复聊持续天数，单位：天
     */
    private Integer rechatDuration;

    /**
     * 创建人的id
     */
    private Integer adminId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime updateTime;

    private Integer relativePositionNums;

    private List<AmChatbotOptionsItemsVo> items;


}
