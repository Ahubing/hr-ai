package com.open.hr.ai.bean.vo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.open.ai.eros.db.mysql.hr.entity.AmPositionPost;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 按照php来实现
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmPositionVo {

    private Integer id;

    /**
     * 所属boss adminid
     */
    private Integer adminId;

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
    private Integer aiAssitantId;

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
    private JSONObject extendParams;

    /**
     * 岗位
     */
    private List<AmPositionPost> amPositionPost;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 通道名称
     */
    private String channelName;


    /**
     * boss 账号
     */
    private String bossAccount;

    /**
     * ai_assitant
     */
    private String aiAssistant;

    /**
     * section
     */
    private String section;

    /**
     * extend_params
     */
    private JSONObject detail;


}
