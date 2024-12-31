package com.open.ai.eros.db.mysql.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 意见反馈表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("feedback")
public class Feedback implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
      private Long id;

    /**
     * 反馈类型id
     */
    private Integer typeId;

    /**
     * 面具id
     */
    private Long maskId;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 星星数
     */
    private Integer star;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime modifyTime;

    /**
     * 备注说明
     */
    private String extra;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 反馈文件附件
     */
    private String fileList;


}
