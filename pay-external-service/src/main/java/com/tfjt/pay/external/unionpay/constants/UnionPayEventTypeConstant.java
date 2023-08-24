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
    /**
     * 转账入金
     */
    public static final String TRANSFER_DEPOSIT = "transfer_deposit";
    /**
     * 提现退汇
     */
    public static final String WITHDRAWAL_RETURN = "withdrawal_return";
    /**
     * 大额订单
     */
    public static final String LARGE_PAYMENT = "large_payment";
    /**
     * 虚户入
     * 金退款退汇
     */
    public static final String TRANSFER_DEPOSIT_REFUND_RETURN = "transfer_deposit_refund_return";
    /**
     * 大额订单退款退汇
     */
    public static final String LARGE_PAYMENT_DEPOSIT_REFUND_RETURN = "large_payment_deposit_refund_return";


    /***
     *  交易结果
     */
    public static final String TRADE_RESULT = "trade_result";


}
