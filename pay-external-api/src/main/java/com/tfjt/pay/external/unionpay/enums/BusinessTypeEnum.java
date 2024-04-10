package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tony
 * @version 1.0
 * @title BusinessTypeEnum
 * @description
 * @create 2024/4/10 15:28
 */
@Getter
@AllArgsConstructor
public enum BusinessTypeEnum {

    //1 云商
    BUSINESS_TYPE(1, "云商"),
    //2 云店
    SHOP_TYPE(2, "云店"),
    //3 实名认证-业务员
    SIGN_USER(3, "实名认证-业务员"),
    //4 实名认证-经销商
    SIGN_BUSINESS(4, "实名认证-经销商");

    private Integer code;
    private String msg;
}
