package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/12 14:58
 * @description 证件类型
 */
@AllArgsConstructor
@Getter
public enum IdTypeEnum {

    ID_CARD(1, "身份证"),
    GANG_AO_RETURN_PERMITS(3, "港澳回乡证"),
    SOCIAL_CREDIT_CODE(73, "统一社会信用代码");

    private final Integer code;
    private final String name;
}
