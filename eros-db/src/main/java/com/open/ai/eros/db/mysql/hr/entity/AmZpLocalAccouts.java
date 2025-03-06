package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 招聘本地账户
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_zp_local_accouts")
public class AmZpLocalAccouts implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键，唯一
     */
    private String id;

    /**
     * 用来验证，为了避免不同平台id类型可能不同。
     */
    private String extBossId;

    /**
     * 管理员id
     */
    private Long adminId;

    /**
     * 平台id
     */
    private Long platformId;

    /**
     * 平台名称
     */
    private String platform;

    /**
     * 账号类型，0本地，1服务端线上
     */
    private Integer type;

    /**
     * 账号
     */
    private String account;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 浏览器id，或者客户端的connect_id
     */
    private String browserId;

    /**
     * 查询boss的account_status返回的用户id
     */
    private Long userId;

    /**
     * 状态，offline下线, wait_login 等待登录 ,free 已经登录成功,没有在执行任务, busy 正在执行任务
     */
    private String state;

    /**
     * 同步状态。0待同步(从未同步)，1同步中,2 已同步
     */
    private int isSync;

    /**
     * 运行状态。服务端脚本使用
     */
    private int isRunning;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 扩展字段,暂时用来存储登录二维码和过期时间
     */
    private String extra;

    /**
     * 是否是第一次登录
     * 0 未登录
     * 1 已登录过
     */
    private Integer logined;


}
