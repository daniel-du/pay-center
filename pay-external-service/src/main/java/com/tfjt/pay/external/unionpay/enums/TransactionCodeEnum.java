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
    LWZ639_COMBINED_GUARANTEE_CONFIRMS_BY_OUT_ORDER_NO("LWZ639_COMBINED_GUARANTEE_CONFIRMS_BY_OUT_ORDER_NO","合并消费担保确认订单查询");

    /**
     * code
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;
}
