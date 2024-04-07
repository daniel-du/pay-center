package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author 21568
 * @create 2024/4/7 16:27
 */
@AllArgsConstructor
@Getter
public enum IncomingChannelEnum {
    PABC("1","平安");



    private String code;
    private String name;


    public static String getNameFromCode(String code){
        for(IncomingChannelEnum inComingAccessTypeEnum : IncomingChannelEnum.values()){
            if(inComingAccessTypeEnum.getCode().equals(code)){
                return inComingAccessTypeEnum.getName();
            }
        }
        return null;
    }


}
