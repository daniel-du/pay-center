package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: BankTypeEnum <br>
 * @date: 2023/5/23 14:43 <br>
 * @author: young <br>
 * @version: 1.0
 */

@AllArgsConstructor
@Getter
public enum BankTypeEnum {
    /**
     * 对私
     */
    PERSONAL("1", "对私账户"),
    /**
     * 对公
     */
    CORPORATE("2", "对公账户");

    /**
     * code
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;
}
