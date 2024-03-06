package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 19:43
 * @description 进件服务类型枚举
 */
@AllArgsConstructor
@Getter
public enum IncomingServiceTypeEnum {

    /**
     * 平安普通类型进件-对公结算
     */
    PINGAN_COMMON_CORPORATE("pingan_common_corporate", ""),

    /**
     * 平安普通类型进件-对私结算
     */
    PINGAN_COMMON_PERSONAL("pingan_common_personal", "");

    private final String incomingType;

    private final String serviceName;

    /**
     * 根据type转换为指定枚举
     * @return
     */
    public static IncomingServiceTypeEnum fromType(String incomingType){
        for(IncomingServiceTypeEnum incomingServiceTypeEnum : IncomingServiceTypeEnum.values()){
            if(incomingServiceTypeEnum.incomingType.equals(incomingType)){
                return incomingServiceTypeEnum;
            }
        }
        return null;
    }
}
