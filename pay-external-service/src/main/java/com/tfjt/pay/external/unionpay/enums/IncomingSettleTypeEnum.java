package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:17
 * @description 进件入网结算类型枚举
 */
@AllArgsConstructor
@Getter
public enum IncomingSettleTypeEnum {

    /**
     * 对公结算
     */
    CORPORATE(1, "corporate"),
    /**
     * 对私结算
     */
    PERSONAL(2, "personal");

    private Integer code;
    private String name;

    /**
     * 根据code转换为指定枚举
     * @return
     */
    public static IncomingSettleTypeEnum fromCode(Integer code){
        for(IncomingSettleTypeEnum inComingSettelTypeEnum : IncomingSettleTypeEnum.values()){
            if(inComingSettelTypeEnum.code.equals(code)){
                return inComingSettelTypeEnum;
            }
        }
        return null;
    }

    /**
     * 根据code转换name
     * @return
     */
    public static String getNameFromCode(Integer code){
        for(IncomingSettleTypeEnum inComingSettleTypeEnum : IncomingSettleTypeEnum.values()){
            if(inComingSettleTypeEnum.code.equals(code)){
                return inComingSettleTypeEnum.name;
            }
        }
        return null;
    }
}
