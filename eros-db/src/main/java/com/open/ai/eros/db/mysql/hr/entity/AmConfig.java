package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 配置表
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_config")
public class AmConfig implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 编号
     */
      @TableId(value = "config_id", type = IdType.AUTO)
    private Integer configId;

    /**
     * 键
     */
    private String key;

    private String value;

    /**
     * 中文说明
     */
    private String comments;


}
