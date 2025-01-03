package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 登录日志
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_log")
public class AmLog implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 登录名称
     */
    private String name;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 登录账户id,管理员0，其他人为企业账户的id
     */
    private Integer userId;

    /**
     * 登录客户的游戏后台id，为不同人分配的id
     */
    private Integer gameUid;

    /**
     * 登录方式，0电脑
     */
    private Boolean type;

    /**
     * 开始时间
     */
    private Integer createTime;


}
