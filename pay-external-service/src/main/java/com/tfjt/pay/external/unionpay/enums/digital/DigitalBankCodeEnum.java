package com.tfjt.pay.external.unionpay.enums.digital;

import lombok.Getter;

/**
 * @author songx
 * @Date: 2023/11/29/10:58
 * @Description:
 */
@Getter
public enum DigitalBankCodeEnum {

    /**工行*/
    DCRI_ICBC("DCRI_ICBC","工行",""),
    /**农行*/
    DCRI_ABC("DCRI_ABC","农行",""),
    /**中行*/
    DCRI_BOC("DCRI_BOC","中行",""),
    /**建行*/
    DCRI_CCB("DCRI_CCB","建行",""),
    /**交行*/
    DCRI_BCM("DCRI_BCM","交行",""),
    /**邮储*/
    DCRI_PSBC("DCRI_PSBC","邮储",""),
    /**网商*/
    DCRI_MYBANK("DCRI_MYBANK","网商",""),
    /**微众*/
    DCRI_WEBANK("DCRI_WEBANK","微众",""),
    /**招行*/
    DCRI_CMB("DCRI_CMB","招行",""),
    /**兴业*/
    DCRI_CIB("DCRI_CIB","兴业","");
    private String code;

    private String desc;

    private String icon;

    DigitalBankCodeEnum(String code, String desc,String icon) {
        this.code = code;
        this.desc = desc;
        this.icon = icon;
    }

    public static DigitalBankCodeEnum getByCode(String code){
        DigitalBankCodeEnum[] values = DigitalBankCodeEnum.values();
        for (DigitalBankCodeEnum value : values) {
            if (value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }
}
