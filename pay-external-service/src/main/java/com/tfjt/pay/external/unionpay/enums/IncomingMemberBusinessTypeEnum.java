package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/6 11:17
 * @description 进件入网商户类型枚举
 */
@AllArgsConstructor
@Getter
public enum IncomingMemberBusinessTypeEnum {

    /**
     * 云商（经销商、供应商）
     */
    YUNSHANG(1, "云商", "TFYS"),
    /**
     * 云店
     */
    YUNDIAN(2, "云店", "TFYD")
    ;

    private Integer code;
    private String name;
    private String memberPrefix;

    /**
     * 根据code转换为指定枚举
     * @return
     */
    public static IncomingMemberBusinessTypeEnum fromCode(Integer code){
        for(IncomingMemberBusinessTypeEnum inComingMemberBusinessTypeEnum : IncomingMemberBusinessTypeEnum.values()){
            if(inComingMemberBusinessTypeEnum.code.equals(code)){
                return inComingMemberBusinessTypeEnum;
            }
        }
        return null;
    }
}
