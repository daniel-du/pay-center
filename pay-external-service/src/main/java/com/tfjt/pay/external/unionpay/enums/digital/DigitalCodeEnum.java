package com.tfjt.pay.external.unionpay.enums.digital;

import lombok.Getter;

/**
 * @author songx
 * @Date: 2023/11/29/11:03
 * @Description:
 */
@Getter
public enum DigitalCodeEnum {

    /**正常推送*/
    VT01("VT01","正常推送"),
    /**切换账号推送*/
    VT02("VT02","切换账号推送"),
    /**拉起推送*/
    VT03("VT03","拉起推送"),
    /**支付并推送*/
    VT04("VT04","支付并推送"),

    /**已注册*/
    EFO0("EFO0","已注册"),
    /**未注册*/
    EFO1("EFO1","未注册"),

 ;
    private String code;

    private String desc;

    DigitalCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
