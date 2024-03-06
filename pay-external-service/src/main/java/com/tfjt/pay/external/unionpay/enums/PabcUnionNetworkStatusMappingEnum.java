package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author zxy
 * @create 2024/1/12 9:41
 */
@AllArgsConstructor
@Getter
public enum PabcUnionNetworkStatusMappingEnum {

    /**
     * 信息填写
     */
    INFORMATION_FILLING("07",1),
    /**
     * 签约中
     */
    SIGNING("01",2),
    /**
     * 开户/签约成功
     */
    SIGNING_SUCCESS("07",3),
    /**
     * 绑卡成功
     */
    CARD_BINDING_SUCCESSFUL("07",4),
    /**
     * 入网审核中
     */
    EXAMING("02",5),
    /**
     * 入网成功
     */
    NETWORK_SUCCESS("03",6),
    /**
     * 入网失败
     */
    NETWORK_FAILURE("04",7),
    /**
     * 对公账户待验证或异常
     */
    TO_BE_VERIFIED("05",8),
    /**
     * 风控审核中
     */
    RISK_CONTROL("06",9),
    /**
     * 短信签生成合同成功
     */
    SUCCESSFULLY_GENERATED_CONTRACT("11",10),
    /**
     * 资料验证失败
     */
    DATA_VERIFICATION_FAILED("28",11),
    /**
     * 短信回填验证成功
     */
    SMS_BACKFILL_VERIFICATION_SUCCESSFUL("07",12),
    /**
     * 冻结账户
     */
    FREEZE_ACCOUNT("04",13);

    private String unionSigningStatus;
    private Integer pabcSignStatus;



    public static String getMsg(Integer code) {
        for (PabcUnionNetworkStatusMappingEnum value : PabcUnionNetworkStatusMappingEnum.values()) {
            if (value.pabcSignStatus.equals(code)) {
                return value.unionSigningStatus;
            }
        }
        return null;
    }


}
