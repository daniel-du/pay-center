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
    DCRI_ICBC("DCRI_ICBC","工商银行","https://qiniu.tongfuyouxuan.com/shop/240124102947-RAHRUr.png"),
    /**农行*/
    DCRI_ABC("DCRI_ABC","农业银行","https://qiniu.tongfuyouxuan.com/shop/240124102823-a8o6K8.png"),
    /**中行*/
    DCRI_BOC("DCRI_BOC","中国银行","https://qiniu.tongfuyouxuan.com/shop/231205141354-ohk3UC.png"),
    /**建行*/
    DCRI_CCB("DCRI_CCB","建设银行","https://qiniu.tongfuyouxuan.com/shop/240124102844-F4JQYs.png"),
    /**交行*/
    DCRI_BCM("DCRI_BCM","交通银行","https://qiniu.tongfuyouxuan.com/shop/240124103011-7Y9K23.png"),
    /**邮储*/
    DCRI_PSBC("DCRI_PSBC","邮储银行","https://qiniu.tongfuyouxuan.com/shop/240124103006-IiEq3m.png"),
    /**网商*/
    DCRI_MYBANK("DCRI_MYBANK","网商银行","https://qiniu.tongfuyouxuan.com/shop/240124103041-JITwQp.png"),
    /**微众*/
    DCRI_WEBANK("DCRI_WEBANK","微众银行","https://qiniu.tongfuyouxuan.com/shop/240124103037-JhwDce.png"),
    /**招行*/
    DCRI_CMB("DCRI_CMB","招行银行","https://qiniu.tongfuyouxuan.com/shop/240124102802-zVxh2s.png"),
    /**兴业*/
    DCRI_CIB("DCRI_CIB","兴业银行","https://qiniu.tongfuyouxuan.com/shop/240124102707-qHHigZ.png");
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
