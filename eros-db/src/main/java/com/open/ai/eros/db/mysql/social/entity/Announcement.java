package com.open.ai.eros.db.mysql.social.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("announcement")
public class Announcement implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键，自增
     */
    private Long id;


    /**
     * 1：系统，2：应用
     */
    private Integer type;

    /**
     * 公告的标题，不能为空
     */
    private String title;

    /**
     * 公告的内容，不能为空
     */
    private String content;

    /**
     * 发布公告的作者，不能为空
     */
    private String createUser;

    /**
     * 发布公告的作者，不能为空
     */
    private String updateUser;

    /**
     * 公告的创建时间，默认为当前时间
     */
    private LocalDateTime createTime;

    /**
     * 公告的修改时间，默认为空
     */
    private LocalDateTime updateTime;

    /**
     *  0-不删除 1-删除
     */
    private Integer status;

    /**
     * 扩展字段
     */
    private String extra;

    private Integer duration;


}
