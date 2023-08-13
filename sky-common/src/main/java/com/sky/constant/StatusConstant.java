package com.sky.constant;

/**
 * 状态常量，启用或者禁用
 */
public class StatusConstant {
    //启用
    public static final Integer ENABLE = 1;

    //禁用
    public static final Integer DISABLE = 0;

    //订单的超时自动取消的时间（单位：分钟）
    public static final Integer ORDER_TIMEOUT_AUTO_CANCEL_TIME = 15;

    //订单的自动完成的时间（单位：小时）
    public static final Integer ORDER_TIMEOUT_AUTO_COMPLETE_TIME = 2;

}
