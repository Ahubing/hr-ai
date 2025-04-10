package com.open.ai.eros.db.mysql.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("knowledge")
public class Knowledge implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 封面
     */
    private String cover;

    /**
     * 简介说明
     */
    private String intro;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 向量模型
     */
    private String templateModel;

    /**
     * 向量数据库
     */
    private String vectorDatabase;


    /**
     * 矢量长度
     */
    private Integer dimension;


    /**
     * 检索最小的得分
     */
    private Double minScore;

    /**
     * 拦截的条数
     */
    private Integer number;

    /**
     * 严格模式 1:严格 2:不严格
     */
    private Integer strict;

}
