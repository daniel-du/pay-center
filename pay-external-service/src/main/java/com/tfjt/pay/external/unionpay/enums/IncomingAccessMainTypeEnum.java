package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/6 11:17
 * @description 进件入网主体类型枚举
 */
@AllArgsConstructor
@Getter
public enum IncomingAccessMainTypeEnum {

    /**
     * 个体工商户
     */
    INDIVIDUAL_BUSINESS(1, "个体工商户"),
    /**
     * 企业
     */
    COMPANY(2, "企业"),
    /**
     * 小微
     */
    SMALL(3,"小微")
    ;

    private Integer code;
    private String name;

    /**
     * 根据code转换为指定枚举
     * @return
     */
    public static IncomingAccessMainTypeEnum fromCode(Integer code){
        for(IncomingAccessMainTypeEnum inComingAccessMainTypeEnum : IncomingAccessMainTypeEnum.values()){
            if(inComingAccessMainTypeEnum.code.equals(code)){
                return inComingAccessMainTypeEnum;
            }
        }
        return null;
    }

}
