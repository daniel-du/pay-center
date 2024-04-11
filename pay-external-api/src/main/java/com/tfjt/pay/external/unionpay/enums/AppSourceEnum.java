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
public enum AppSourceEnum {

    //1 福商通
    FST(1,"福商通"),
    //2 福战通
    FZT(2,"福战通");

    private Integer code;
    private String msg;
}
