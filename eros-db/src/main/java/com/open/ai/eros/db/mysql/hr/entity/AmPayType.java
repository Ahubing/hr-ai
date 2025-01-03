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
 * 系统收款方式
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_pay_type")
public class AmPayType implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "pay_id", type = IdType.AUTO)
    private Integer payId;

    /**
     * 账号
     */
    private String account;

    /**
     * 收款户名
     */
    private String accountName;

    /**
     * 收款银行名称
     */
    private String bankName;

    /**
     * 微信或者支付宝收款码地址
     */
    private String qrCodeUrl;

    /**
     * 收款方式类型1微信2支付宝3银行
     */
    private Boolean type;

    /**
     * 1正常，2禁用
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private Integer createTime;

    /**
     * 更新时间
     */
    private Integer updateTime;


}
