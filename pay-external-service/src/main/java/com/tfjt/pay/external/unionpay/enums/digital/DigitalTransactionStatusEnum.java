package com.tfjt.pay.external.unionpay.enums.digital;

import lombok.Getter;

/**
 * 数字人民币交易响应码
 * @author songx
 * @Date: 2023/11/29/10:53
 */
@Getter
public enum DigitalTransactionStatusEnum {
    /**
     * 数字人民币响应成功
     */
    DIGITAL_SUCCESS("PR00","成功"),
    /**
     * 账户不存在
     */
    ACCOUNT_NOT_EXIST("R021","账户不存在"),

    /**
     * 数字人民币响应失败
     */
    DIGITAL_FAILED("PR01","失败"),
    /**
     * 数字人民币响应处理中
     */
    DIGITAL_PROCESSING("PR01","处理中"),
    /**
     * 未实名认证
     */
    DIGITAL_NOT_REAL_NAME("R183","未实名认证");

    private String code;

    private String desc;

    DigitalTransactionStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
