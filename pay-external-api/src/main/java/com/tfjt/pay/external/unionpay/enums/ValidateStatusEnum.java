package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tony
 * @version 1.0
 * @title ValidateStatusEnum
 * @description
 * @create 2023/10/19 10:16
 */

@Getter
@AllArgsConstructor
public enum ValidateStatusEnum {
    /**
     * 打款验证
     */
    YES(1,"是"),
    NO(0,"否");
    private Integer code;
    private String desc;
}
