package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tony
 * @version 1.0
 * @title DepositTypeEnum
 * @description
 * @create 2023/10/19 13:59
 */
@Getter
@AllArgsConstructor
public enum DepositTypeEnum {
    //充值
    DEPOSIT("1","充值"),
    //消费
    CONSUME("2","消费");

    private String code;

    private String des;
}
