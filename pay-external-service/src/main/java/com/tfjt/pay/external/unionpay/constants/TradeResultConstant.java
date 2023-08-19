package com.tfjt.pay.external.unionpay.constants;

/**
 * @author songx
 * @date 2023-08-18 17:44
 * @email 598482054@qq.com
 */
public class TradeResultConstant {
    // 银联交易结果
    /**
     * 未知
     */
    public static final String UNIONPAY_UNKNOWN = "unknown";

    /***
     *  交易结果 成功
     */
    public static final String UNIONPAY_SUCCEEDED = "succeeded";

    /***
     *  交易结果 失败
     */
    public static final String UNIONPAY_FAILED = "failed";

    /***
     *  交易结果 部分成功
     */
    public static final String UNIONPAY_PARTIALLY_SUCCEEDED = "partially_succeeded";




    // pay 交易结果
    /**
     * 交易成功
     */
    public static final String PAY_SUCCESS = "TRADE_SUCCESS";

    /**
     * 交易失败
     */
    public static final String PAY_FAILED = "TRADE_ERROR";
}
