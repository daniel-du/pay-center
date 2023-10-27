package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *@title TransactionCodeEnum
 *@description 银联商户资金自主管理交易码
 *@author A1583
 *@version 1.0
 *@Date 2023/8/9 9:44
 */
@AllArgsConstructor
@Getter
public enum TransactionCodeEnum {

    /**
     * 合并消费担保下单
     */
    LWZ634_COMBINED_GUARANTEE_PAYMENTS("LWZ634_COMBINED_GUARANTEE_PAYMENTS","合并消费担保下单"),

    /**
     * 合并消费担保确认
     */
    LWZ637_COMBINED_GUARANTEE_CONFIRMS("LWZ637_COMBINED_GUARANTEE_CONFIRMS","合并消费担保确认"),

    /**
     * 电子账簿流水查询(确认后查询订单状态)
     */
    LWZ623_BALANCE_TRANSACTIONS_REQ("LWZ623_BALANCE_TRANSACTIONS_REQ","电子账簿流水查询"),

    /**
     * 合并消费担保订单查询(确认前查询订单状态)
     */
    LWZ636_COMBINED_GUARANTEE_PAYMENTS_BY_OUT_ORDER_NO("LWZ636_COMBINED_GUARANTEE_PAYMENTS_BY_OUT_ORDER_NO","合并消费担保订单查询"),

    /**
     * 合并消费担保确认订单查询(确认后查询订单状态)
     */
    LWZ639_COMBINED_GUARANTEE_CONFIRMS_BY_OUT_ORDER_NO("LWZ639_COMBINED_GUARANTEE_CONFIRMS_BY_OUT_ORDER_NO","合并消费担保确认订单查询"),
    /**
     *
     */
    LWZ511_RECEIPT_QUERY_REQ("LWZ511_RECEIPT_QUERY_REQ","电子账簿查询(电子账簿ID"),

    /**
     * 提现创建
     */
    LWZ64_WITHDRAWALS_REQ("LWZ64_WITHDRAWALS_REQ","提现创建"),
    /**分账创建*/
    LWZ616_ALLOCATIONS("LWZ616_ALLOCATIONS","分账创建"),
    /**下载电子对账单*/
    LWZ91_RECEIPT_QUERY_REQ("LWZ91_RECEIPT_QUERY_REQ","现在电子对账单"),

    /**使用“平台订单号”查询提现订单状态*/
    LWZ66_WITHDRAWALS_BY_OUT_ORDER_NO("LWZ66_WITHDRAWALS_BY_OUT_ORDER_NO","使用“平台订单号”查询提现订单状态"),

    LWZ61_DEPOSIT_REQ("LWZ61_DEPOSIT_REQ","支付充值"),

    LWZ63_DEPOSIT_QRY_REQ("LWZ63_DEPOSIT_QRY_REQ","支付充值订单查询(平台订单号)");
    /**
     * code
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;
}
