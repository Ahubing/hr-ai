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
 * 方案选项
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_chatbot_options_items")
public class AmChatbotOptionsItems implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的方案id
     */
    private Integer optionId;

    /**
     * 第几天
     */
    private Integer dayNum;

    /**
     * 执行时间。根据day_num区分。如第一天30分钟,填1800，单位为秒；第二天则填09:00这类时分，定时执行
     */
    private String execTime;

    /**
     * 回复类型。text表示文本回复，img表示图片回复，ai表示AI回复
     */
    private String replyType;

    /**
     * ai角色，类型为ai时上传
     */
    private String aiRole;

    /**
     * 话术内容
     */
    private String content;

    /**
     * 追问内容
     */
    private Object repeatContent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
