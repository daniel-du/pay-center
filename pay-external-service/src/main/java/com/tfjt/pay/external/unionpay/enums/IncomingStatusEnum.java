package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum IncomingStatusEnum {

    /**
     * 未入网
     */
    NOT_INCOMING("0", "未入网"),
    /**
     * 已入网
     */
    INCOMING("1", "已入网");

    private final String code;
    private final String name;

    /**
     * 根据code转换为指定枚举
     * @return
     */
    public static IncomingStatusEnum fromCode(Integer code){
        for(IncomingStatusEnum inComingAccessTypeEnum : IncomingStatusEnum.values()){
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
        for(IncomingStatusEnum inComingAccessTypeEnum : IncomingStatusEnum.values()){
            if(inComingAccessTypeEnum.code.equals(code)){
                return inComingAccessTypeEnum.name;
            }
        }
        return null;
    }
}
