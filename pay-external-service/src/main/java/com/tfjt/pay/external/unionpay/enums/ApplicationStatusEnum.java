package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tony
 * @version 1.0
 * @title ApplicationStatusEnum
 * @description
 * @create 2023/8/23 16:16
 */
@AllArgsConstructor
@Getter
public enum ApplicationStatusEnum {
    //checking：资料校验中
    //account_need_verify：待账户验证(四要素鉴权)
    //auditing：审核中
    //processing：处理中
    //signing:电子签约中
    //succeeded：已通过
    //failed：被驳回
    //unprocessed:未进件
    //frozen:已冻结
    CHECKING("checking", "资料校验中"),
    ACCOUNT_NEED_VERIFY("account_need_verify", "待账户验证(四要素鉴权)"),
    AUDITING("auditing", "审核中"),
    PROCESSING("processing", "处理中"),
    SIGNING("signing", "电子签约中"),
    SUCCEEDED("succeeded", "已通过"),
    FAILED("failed", "被驳回"),
    UNPROCESSED("unprocessed", "未进件"),
    FROZEN("frozen", "已冻结");

    private String code;
    private String desc;
}
