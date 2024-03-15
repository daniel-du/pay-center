package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:17
 * @description 进件入网类型枚举
 */
@AllArgsConstructor
@Getter
public enum IncomingAccessTypeEnum {

    /**
     * 贷款进件
     */
    LOAN(1, "loan"),
    /**
     * 普通进件
     */
    COMMON(2, "common");

    private Integer code;
    private String name;

    /**
     * 根据code转换为指定枚举
     * @return
     */
    public static IncomingAccessTypeEnum fromCode(Integer code){
        for(IncomingAccessTypeEnum inComingAccessTypeEnum : IncomingAccessTypeEnum.values()){
            if(inComingAccessTypeEnum.code.equals(code)){
                return inComingAccessTypeEnum;
            }
        }
        return null;
    }

    /**
     * 根据code转换name
     * @return
     */
    public static String getNameFromCode(Integer code){
        for(IncomingAccessTypeEnum inComingAccessTypeEnum : IncomingAccessTypeEnum.values()){
            if(inComingAccessTypeEnum.code.equals(code)){
                return inComingAccessTypeEnum.name;
            }
        }
        return null;
    }
}
