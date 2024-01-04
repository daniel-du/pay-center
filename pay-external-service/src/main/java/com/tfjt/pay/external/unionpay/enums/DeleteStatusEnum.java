package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author zxy
 * @create 2024/1/2 16:39
 */
@Getter
@AllArgsConstructor
public enum DeleteStatusEnum {
    YES(1, "是"),
    NO(0, "否");


    private final Integer code;
    private final String name;
}
