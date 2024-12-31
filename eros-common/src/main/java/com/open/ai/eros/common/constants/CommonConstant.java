package com.open.ai.eros.common.constants;

/**
 * @类名：UserConstant
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 14:54
 */
public class CommonConstant {


    /**
     * 订单商品发放队列
     */
    public final static String ORDER_GOODS_PROVIDE_SET = "order:goods:provide:set";

    /**
     * 用户的余额的同步队列
     */
    public final static String USER_BALANCE_SYNC = "user:balance:sync:set%s";


    /**
     * 用户的权益的同步队列
     */
    public final static String USER_RIGHTS_SYNC = "user:rights:sync:set";

    /**
     * 用户权益集合
     */
    public final static String USER_RIGHTS_SET = "user:rights:set:%s:%s";

    /**
     * 用户权益
     */
    public final static String USER_RIGHTS_KEY = "user:rights:%s";


    public final static String USER_BALANCE_KEY = "user:balance:%s";


    public final static String USER_LOGIN_TOKEN = "ErosAIToken";


    /**
     * redis的key
     */
    public static final String CODE_KEY = "code:%s";


    /**
     * redis的token的key
     */
    public static final String USER_LOGIN_TOKEN_KEY = "USER_LOGIN_TOKEN:%s";


    /**
     * 登录失效三小时
     */
    public static final Long TOKEN_TIME_OUT = 720 * 3600L;


    /**
     * 十的 7次方为  1美元
     */
    public static final Integer ONE_DOLLAR = 10000000;


    /**
     * 用户的初始化余额 0.1刀
     */
    public static final Long userInitBalance = 1000000L;


    /**
     * 最小的计费额度  0.001刀
     */
    public static final Long MINI_BILLING_BALANCE = 10000L;


    /**
     * 最小的分红额度  0.0001刀
     */
    public static final Long SHARE_MINI_BILLING_BALANCE = 1000L;


    /**
     * 不可提现
     */
    public static final Integer nonWithdrawableBalanceType = 1;


    /**
     *  可提现
     */
    public static final Integer drawableBalanceType = 2;

}
