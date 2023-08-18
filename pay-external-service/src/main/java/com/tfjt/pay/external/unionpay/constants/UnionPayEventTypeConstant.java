package com.tfjt.pay.external.unionpay.constants;

/**
 * @author songx
 * @date 2023-08-18 16:14
 * @email 598482054@qq.com
 */
public class UnionPayEventTypeConstant {
    /**
     * 二级商户进件结果通知
     */
    public static final String MCH_APPLICATION_FINISHED = "mch_application_finished";
    /**
     *  打款验证通知
     */
    public static final String SETTLE_ACCT_PAY_AMOUNT_VALIDATION = "settle_acct_pay_amount_validation";
    /**
     * 母户入金
     */
    public static final String ROOT_TRANSFER_DEPOSIT = "root_transfer_deposit";
    /***
     *  交易结果
     */
    public static final String TRADE_RESULT = "trade_result";


}
