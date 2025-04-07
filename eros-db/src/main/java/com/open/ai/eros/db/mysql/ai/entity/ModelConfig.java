package com.open.ai.eros.db.mysql.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.open.ai.eros.db.privacy.annotation.FieldEncrypt;
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
 * @author Eros-AI
 * @since 2024-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("model_config")
public class ModelConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * ai模型 以逗号分开
     */
    private String templateModel;

    private String name;

    /**
     * 调用的代理地址
     */
    private String baseUrl;

    /**
     * 已用余额
     */
    private Long usedBalance;

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 访问token
     */
    @FieldEncrypt
    private String token;

    /**
     * 备注说明
     */
    private String extra;

    /**
     * 状态 0 ：管理员手动禁用 1：开启  2: 等待系统自动开启渠道 3：系统自动置为失效
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createAccount;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private String updateAccount;


    /**
     * 倍率
     */
    private double multiple;


}

