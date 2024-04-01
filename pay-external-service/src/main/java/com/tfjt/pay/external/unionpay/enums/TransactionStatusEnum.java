package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lzh
 * @version 1.0
 * @title TransactionStatusEnum
 * @description 交易状态
 * @Date 2023/8/14 16:00
 */
@AllArgsConstructor
@Getter
public enum TransactionStatusEnum {
    /**
     * 贷款成功
     */
    SUCCEEDED("succeeded","成功"),
    /**
     * 贷款失败
     */
    FAILED("failed","失败"),
    /**
     * 贷款部分成功
     */
    PARTIALLY_SUCCEEDED("partially_succeeded","部分成功");

    /**
     * code
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;
}
