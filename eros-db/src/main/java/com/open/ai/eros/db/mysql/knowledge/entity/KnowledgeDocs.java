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
@TableName("knowledge_docs")
public class KnowledgeDocs implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 知识库id
     */
    private Long knowledgeId;

    /**
     * 类型
     */
    private String type;

    /**
     * url
     */
    private String url;


    /**
     * 切片数量
     */
    private Integer sliceNum;

    /**
     *  是否已经切片  1：未切分  2：已经切分
     */
    private Integer sliceStatus;

    /**
     * 文档内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 切割规则
     */
    private String sliceRule;

    /**
     * 切割方式
     */
    private String splitterType;

}
