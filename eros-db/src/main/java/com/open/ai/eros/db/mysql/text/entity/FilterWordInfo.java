package com.open.ai.eros.db.mysql.text.entity;

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
 * @author lixin
 * @since 2023-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("filter_word_info")
public class FilterWordInfo implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    private String wordContent;

    /**
     * 通道id信息，以逗号开头，逗号结尾：,12312,123212,
     */
    private String channelStr;

    /**
     * 语言
     */
    private String language;

    /**
     * 敏感词类型 1：风险词，2：url 3 全拼  4：白词
     */
    private Integer type;

    /**
     * 风险等级
     */
    private Integer riskType;

    /**
     * 风险等级
     */
    private Integer riskLevel;

    /**
     * 状态 1：启动  0 禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private Long createUserId;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人create_user_id
     */
    private Long updateUserId;

    /**
     * 自动回复的id
     */
    private Long replyId;

}
