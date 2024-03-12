package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/6 20:17
 * @description 进件入网渠道类型枚举
 */
@AllArgsConstructor
@Getter
public enum IncomingAccessChannelTypeEnum {

    /**
     * 平安
     */
    PINGAN(1, "pingan", "平安"),
    /**
     * 银联
     */
    UNIONPAY(2, "unionpay", "银联")
    ;

    private Integer code;
    private String name;
    private String desc;

    /**
     * 根据code转换为指定枚举
     * @return
     */
    public static IncomingAccessChannelTypeEnum fromCode(Integer code){
        for(IncomingAccessChannelTypeEnum inComingAccessChannelTypeEnum : IncomingAccessChannelTypeEnum.values()){
            if(inComingAccessChannelTypeEnum.code.equals(code)){
                return inComingAccessChannelTypeEnum;
            }
        }
        return null;
    }

    /**
     * 根据code转换name
     * @return
     */
    public static String getNameFromCode(Integer code){
        for(IncomingAccessChannelTypeEnum inComingAccessChannelTypeEnum : IncomingAccessChannelTypeEnum.values()){
            if(inComingAccessChannelTypeEnum.code.equals(code)){
                return inComingAccessChannelTypeEnum.name;
            }
        }
        return null;
    }

    /**
     * 根据code转换desc
     * @return
     */
    public static String getDescFromCode(Integer code){
        for(IncomingAccessChannelTypeEnum inComingAccessChannelTypeEnum : IncomingAccessChannelTypeEnum.values()){
            if(inComingAccessChannelTypeEnum.code.equals(code)){
                return inComingAccessChannelTypeEnum.desc;
            }
        }
        return null;
    }
}
