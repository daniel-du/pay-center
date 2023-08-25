package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tony
 * @version 1.0
 * @title LoanUserTypeEnum
 * @description
 * @create 2023/8/25 10:27
 */
@Getter
@AllArgsConstructor
public enum LoanUserTypeEnum {
    /**
     *  0=个人进件
     *  1-企业
     *  2-个体工商户
     */
    PERSONAL(0,"个人进件"),
    COMPANY(1,"企业"),
    ENTERPRISE(2,"个体工商户");

    private Integer code;
    private String desc;
}
