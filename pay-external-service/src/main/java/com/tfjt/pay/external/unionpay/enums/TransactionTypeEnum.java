package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lzh
 * @version 1.0
 * @title TransactionTypeEnum
 * @description 交易类型
 * @Date 2023/8/12 16:11
 */
@AllArgsConstructor
@Getter
public enum TransactionTypeEnum {

    ROOTACCTDEPOSIT(	10,"母户入金"),

    RELACCTDEPOSIT(	11,"转账入金"),

    CREDIT(	12,"入账"),

    LARGEPAYMENT(	13,"大额订单"),

    DEPOSIT(	20,"支付充值"),

    DEPOSITREFUND(	21,"退款(支付充值)"),

    DISCOUNT(	22,"营销"),

    DISCOUNTREFUND(	23,"营销退款"),

    DISCOUNTCREDIT(	24,"营销入账"),

    WITHDRAWABLEDEPOSIT(	25,"可提现充值"),

    WITHDRAWABLEDEPOSITREFUND(	26,"可提现充值退款"),

    WITHDRAWAL(	30,"提现发起"),

    WITHDRAWALSUCCEEDED(	31,"提现(成功)"),

    WITHDRAWALFAILED(	32,"提现失败"),

    WITHDRAWALRETURN(	33,"提现退汇"),

    RELACCTDEPOSITREFUND(	35,"转账入金原路退回"),

    RELACCTDEPOSITREFUNDRETURN(	37,"转账入金原路退回退汇"),

    LARGEPAYMENTREFUND(	38,"大额订单入金原路退回"),

    LARGEPAYMENTREFUNDRETURN(	39,"大额订单入金退回退汇"),

    PAYMENT(	40,"消费"),

    PAYMENTREFUND(	41,"退款(消费)"),

    RELACCTDEPOSITPREV(	42,"转账入金前置"),

    LARGEPAYMENTPREV(	43,"大额入金前置"),

    RELACCTDEPOSITREFUNDFAILED(	44,"转账入金退款失败"),

    RELACCTDEPOSITREFUNDSUCCEEDED(	45,"转账入金退款成功"),

    LARGEPAYMENTREFUNDFAILED(	46,"大额订单入金退款失败"),

    LARGEPAYMENTREFUNDSUCCEEDED(	47,"大额订单入金退款成功"),

    ALLOCATION(	51,"分账"),

    GUARANTEEPAYMENT(	60,"担保下单"),

    GUARANTEECONFIRM(	61,"担保确认"),

    GUARANTEEPAYMENTREFUND(	62,"退款(担保下单)"),

    GUARANTEECONFIRMREFUND(	63,"退款(担保确认)"),

    GUARANTEEDEPOSIT(	64,"担保支付"),

    GUARANTEEDEPOSITCONFIRM(	65,"担保支付确认"),

    GUARANTEEDEPOSITREFUND(	66,"担保支付退款"),

    GUARANTEEDEPOSITCONFIRMREFUND(	67,"担保支付确认退款"),

    BALANCEFREEZE(	70,"余额冻结"),

    BALANCEUNFREEZE(	71,"余额解冻"),

    ADJUST(	72,"调账");



    /**
     * code
     */
    private final Integer code;
    /**
     * 描述
     */
    private final String desc;
}
