package com.open.ai.eros.db.mysql.ai.entity;

import com.open.ai.eros.db.privacy.annotation.FieldEncrypt;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 模型配置
 * </p>
 *
 * @author Administrator
 * @since 2023-09-09
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModelConfigVo {

    /**
     * 访问的地址
     */
      private Long id;

    /**
     * 访问地址
     */
    private String baseUrl;

    /**
     * 访问token
     * token字段加密
     */
    @FieldEncrypt
    private String token;

    /**
     * 渠道模型
     */
    private List<String> templateModel;

    /**
     * 渠道名称
     */
    private String name;

    /**
     * 使用余额
     */
    private Long usedBalance;
    /**
     * 使用余额
     */
    private String usedBalanceStr;


    /**
     * 倍率
     */
    private double multiple;

    /**
     * 调用权重
     */
    private Integer weight;

    /**
     * 状态 0 ：管理员手动禁用 1：开启  2: 等待系统自动开启渠道 3：系统自动置为失效
     */
    private Integer status;

    /**
     * 扩展字段
     */
    private String extra;


    private String createAccount;


}
