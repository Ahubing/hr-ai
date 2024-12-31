package com.open.ai.eros.db.mysql.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 文档切片表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("docs_slice")
public class DocsSlice implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
      private Long id;

    /**
     * 向量库的ID
     */
    private String vectorId;

    /**
     * 跟随文档的type
     */
    private String type;

    /**
     * 文档ID
     */
    private Long docsId;

    /**
     * 知识库ID
     */
    private Long knowledgeId;

    /**
     * 文档名称
     */
    private String name;

    /**
     * 切片内容
     */
    private String content;

    /**
     * 字符数
     */
    private Integer wordNum;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 状态 1: 向量化  2：未向量
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
