package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tony
 * @version 1.0
 * @title UnionPayStatusEnum
 * @description
 * @create 2024/3/27 15:33
 */
@AllArgsConstructor
@Getter
public enum UnionPayStatusEnum {
    //-1:未签约
    UNSIGNED("-1", "未签约"),
    //00:签约中
    SIGNING("00", "签约中"),
    //01:签约成功
    SIGNED("01", "签约成功"),
    //02:入网审核中
    SIGNING_IN("02", "入网审核中"),
    //03:入网成功
    SIGNING_SUCCESS("03", "入网成功"),
    //04:入网失败否
    SIGNING_FAIL("04", "入网失败"),
    //05:对公账户待验证或异常
    SIGNING_ABNORMAL("05", "对公账户待验证或异常"),
    //06:风控审核中
    SIGNING_RISK("06", "风控审核中"),
    //11:短信签生成合同成功
    SIGNING_CONTRACT("11", "短信签生成合同成功"),
    //18:资料填写中
    SIGNING_FILL("18", "资料填写中"),
    //31:冻结账户
    FREEZE("31", "冻结账户"),
    //32:客服视频核验中
    VIDEO_CHECKING("32", "客服视频核验中"),
    //33:客服视频核验失败
    VIDEO_CHECK_FAIL("33", "客服视频核验失败"),
    //34:待客户经理补充材料
    SIGNING_MATERIAL("34", "待客户经理补充材料"),
    //99:其他错误否
    ERROR("99", "其他错误"),
    //35:DM 已冻结
    DM_FREEZE("35", "DM 已冻结"),
    //30:撤销状态
    CANCEL("30", "撤销状态"),
    ;

    /**
     * code
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;

    public static String getDesc(String code) {
        for (UnionPayStatusEnum incomingServiceTypeEnum : UnionPayStatusEnum.values()) {
            if (incomingServiceTypeEnum.getCode().equals(code)) {
                return incomingServiceTypeEnum.getDesc();
            }
        }
        return "其他错误";
    }

}
