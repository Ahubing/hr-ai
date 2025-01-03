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
 * 用户钱包使用记录表
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mini_uni_user_wallet_log")
public class MiniUniUserWalletLog implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商户id
     */
    private String mid;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 余额，以分为单位
     */
    private Integer balance;

    /**
     * 操作前余额
     */
    private Integer originBalance;

    /**
     * 变化金额
     */
    private Integer changeAmount;

    /**
     * 0充值，1提现
     */
    private Boolean type;

    /**
     * 备注，描述
     */
    private String note;

    private Integer updateTime;

    /**
     * 创建时间
     */
    private Integer createTime;


}
