package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: OcrTypeEnum <br>
 * @date: 2023/5/20 15:14 <br>
 * @author: young <br>
 * @version: 1.0
 */

@AllArgsConstructor
@Getter
public enum OcrTypeEnum {

    BANK("bank","银行卡"),

    IDCARD("idcard","身份证");

    /**
     * code
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;
}
