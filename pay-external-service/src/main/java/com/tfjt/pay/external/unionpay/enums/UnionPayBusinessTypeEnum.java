package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tony
 * @version 1.0
 * @title UnionPayBusinessTypeEnum
 * @description
 * @create 2023/8/17 08:35
 */
@Getter
@AllArgsConstructor
public enum UnionPayBusinessTypeEnum {
    WITHDRAWAL("1", "提现"),
    TRANSFER("2", "转账"),
    UNIFIEDORDER("3","下单");
    private final String code;
    private final String desc;
}
