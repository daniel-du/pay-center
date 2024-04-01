package com.tfjt.pay.external.unionpay.enums.digital;

import lombok.Getter;

/**
 * 证件类型
 * @create 2024/01/26/14:24
 * @author songx
 * @Description:
 */
@Getter
public enum DigitalCertTypeEnum {

    /**身份证号*/
    IT01("IT01","居民身份证");
    /**
     * 编码
     */
    private String code;
    /**
     * 描述
     */
    private String desc;

    DigitalCertTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
