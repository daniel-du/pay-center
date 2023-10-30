package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tony
 * @version 1.0
 * @title BankAcctTypeEnum
 * @description
 * @create 2023/10/19 11:20
 */
@Getter
@AllArgsConstructor
public enum BankAcctTypeEnum {

    //对私
    PRIVATE("1","对私"),
    //对公
    PUBLIC("2","对公"),
    //母户的账号
    PARENT("3","母户的账号"),
    //验资账户户号
    VERIFICATION("4","验资账户户号");

    private String code;

    private String desc;

}
