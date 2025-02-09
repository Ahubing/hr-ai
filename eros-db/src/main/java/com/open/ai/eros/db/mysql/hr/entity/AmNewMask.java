package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 面具
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_new_mask")
public class AmNewMask implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 面具名称
     */
    private String name;

    /**
     * 面具类别 普通面具：mask   分析: share_mask
     */
    private String type;

    /**
     * 模型来源  aws  az gpt  claude
     */
    private String templateModel;

    /**
     * 面具的简单说明
     */
    private String intro;

    /**
     * 用户ID
     */
    private Long adminId;

    /**
     * 记忆上下文条数
     */
    private Integer contentsNumber;

    /**
     * 面具的标签   逗号分开  游戏,性感
     */
    private String tags;

    /**
     * 状态  1 发布 2 待发布
     */
    private Integer status;

    /**
     * ai请求参数,用于后续拼接成json串
     */
    private String aiRequestParam;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 头像
     */
    private String avatar;


}
