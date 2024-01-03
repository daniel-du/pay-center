package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/20 16:29
 * @description 平安api枚举
 */
@Getter
@AllArgsConstructor
public enum PnApiEnum {

    /**
     * 开户
     */
    OPEN_ACCOUNT("KFEJZB6248", "/V1.0/AutonymOpenCustAcctId"),

    /**
     * 绑定银行卡-对私
     */
    BIND_CARD_PERSONAL("KFEJZB6238", "/V1.0/BindUnionPayWithCheckCorp"),

    /**
     * 验证码回填-对私
     */
    CHECK_CODE_PERSONAL("KFEJZB6239", "/V1.0/CheckMsgCodeWithCorp"),

    /**
     * 绑定银行卡-对公
     */
    BIND_CARD_CORPORATE("KFEJZB6240", "/V1.0/BindSmallAmountWithCheckCorp"),

    /**
     * 验证码、金额回填-对公
     */
    CHECK_CODE_CORPORATE("KFEJZB6241", "/V1.0/CheckAmountWithCorp"),

    /**
     * 验证协议
     */
    REGISTER_BEHAVIOR("KFEJZB6244", "/V1.0/RegisterBehaviorRecordInfo");


    /**
     * 接口id
     */
    private String serviceCode;

    /**
     * 服务id
     */
    private String serviceId;
}
