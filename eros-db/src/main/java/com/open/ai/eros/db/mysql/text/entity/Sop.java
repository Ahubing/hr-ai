package com.open.ai.eros.db.mysql.text.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2024-10-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sop")
public class Sop implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 场景
     */
    private String sceneInfo;

    /**
     * 场景定义
     */
    private String sceneDesc;

    /**
     * 对话流程
     */
    private String sop;

    private LocalDateTime createTime;

    /**
     * 编码
     */
    private String code;


}
