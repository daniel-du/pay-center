package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author zxy
 * @create 2024/1/9 16:45
 */
@Getter
@AllArgsConstructor
public enum SupplierTypeEnum {

    //供应商
    SUPPLIER(1,"供应商"),
    //经销商
    DEALER(2,"经销商");

    private Integer code;

    private String desc;


}
