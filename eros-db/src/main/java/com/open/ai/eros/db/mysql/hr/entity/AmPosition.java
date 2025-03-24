package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_position")
public class AmPosition implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属大账户id
     */
    private Long adminId;

    /**
     * 职位名称
     */
    private String name;

    /**
     * 部门id
     */
    private Integer sectionId;

    /**
     * 岗位id
     */
    private Integer postId;

    /**
     * 用户id，招聘人员的id
     */
    private Integer uid;

    /**
     * account_id,boss_id
     */
    private String bossId;

    /**
     * 脚本返回的jobid
     */
    @TableField("jobId")
    private Integer jobId;

    /**
     * 职位表返回的加密id，可用来更新职位
     */
    @TableField("encryptId")
    private String encryptId;

    /**
     * 渠道，platform的id
     */
    private Long channel;

    /**
     * 招聘状态 1运行中，0暂停
     */
    private Boolean status;

    /**
     * ai助手id
     */
    private Long aiAssitantId;

    /**
     * 是否开放职位
     */
    private Integer isOpen;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 拓展字段，json保存职位数据
     */
    private String extendParams;

    /**
     * 是否正在更改中 0否 1是
     */
    private Integer isSyncing;

    /**
     * 是否删除 0否 1是
     */
    private Integer isDeleted;

    /**
     * 岗位描述
     */
    private String amDescribe;

    /**
     * 评分标准+人才画像
     */
    private String jobStandard;

    /**
     * 城市
     */
    private String city;


}
