package com.tfjt.pay.external.unionpay.constants;

/**
 * redis 常量信息
 * @author songx
 * @date 2023-08-14 14:02
 * @email 598482054@qq.com
 */
public class RedisConstant {
    /**
     * 交易订单号
     */
    public static final String PAY_GENERATE_ORDER_NO = "pay:generate_order_no";

    public static final String MOBILE_VERIFICATION_CODE = "smsCode:";

    public static final String NETWORK_TYPE_BY_AREA_CODE = "pay:incoming:";
    public static final String NETWORK_TYPE_BY_AREA_CODE_All = "pay:incoming:all";

    /**
     * 进件信息缓存key前缀
     */
    public static final String INCOMING_MSG_KEY_PREFIX = "PAY:EXTERNAL:INCOMING:";

    /**
     * 进件信息缓存key前缀
     */
    public static final String INCOMING_BINK_CARD_KEY_PREFIX = "PAY:EXTERNAL:INCOMING:BINKCARD:";
}
