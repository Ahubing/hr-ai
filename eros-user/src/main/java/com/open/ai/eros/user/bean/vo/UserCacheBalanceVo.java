package com.open.ai.eros.user.bean.vo;

import lombok.Data;

/**
 * @类名：UserBalanceVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/13 23:37
 */
@Data
public class UserCacheBalanceVo {

    /**
     * 可提现金额
     */
    private Long withDrawable;

    /**
     * 不可提现金额
     */
    private Long noWithDrawable;


}
