package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 面试日历-预约记录
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ic_record")
public class IcRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 受聘者uid
     */
    private String employeeUid;

    /**
     * 管理员/招聘方id
     */
    private Long adminId;

    /**
     * 面试开始时间
     */
    private LocalDateTime startTime;

    /**
     * 面具id
     */
    private Long maskId;

    /**
     * 面试类型single-单面，group-群面
     */
    private String interviewType;

    /**
     * 取消状态1-未取消，2-已取消
     */
    private Integer cancelStatus;

    /**
     * 谁取消了，1-招聘方，2-受聘方
     */
    private Integer cancelWho;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * boss_id，账号id
     */
    private String accountId;

    /**
     * 职位id
     */
    private Long positionId;

    /**
     * 职位名称
     */
    private String positionName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 应聘者姓名
     */
    private String employeeName;

    /**
     * 平台
     */
    private String platform;


    /**
     * 账号
     */
    private String account;

}
