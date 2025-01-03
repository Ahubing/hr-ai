package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("am_chatbot_options")
public class AmChatbotOptions implements Serializable {

    private static final long serialVersionUID=1L;

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
    private Boolean type;

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
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
