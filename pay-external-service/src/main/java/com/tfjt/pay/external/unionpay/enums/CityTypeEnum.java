package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author zxy
 * @create 2024/1/5 16:12
 */
@AllArgsConstructor
@Getter
public enum CityTypeEnum {
    /**
     * 老城
     */
    OLD_CITY(2, "银联"),
    /**
     * 新城
     */
    NEW_CITY(1, "平安");

    /**
     * code
     */
    private final Integer code;
    /**
     * 描述
     */
    private final String desc;
}
