package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tony
 * @version 1.0
 * @title BusinessUserTypeEnum
 * @description
 * @create 2023/10/20 10:44
 */
@Getter
@AllArgsConstructor
public enum BusinessUserTypeEnum {
    //商家
    BUSINESS(1,"商家"),
    //供应商
    SUPPLIER(2,"供应商"),
    //经销商
    DEALER(3,"经销商");

    private Integer code;

    private String desc;
}
