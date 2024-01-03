package com.tfjt.pay.external.unionpay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/3 13:44
 * @description 进件状态枚举
 */
@AllArgsConstructor
@Getter
public enum IncomingAccessStatusEnum {

    IMPORTS_CLOSURE(0, "已导入"),

    MESSAGE_FILL_IN(1, "信息填写"),

    SIGNING(2, "签约中"),

    SIGN_SUCCESS(3, "签约/开户成功"),

    BINK_CARD_SUCCESS(4, "绑卡成功"),

    ACCESS_AUDIT(5, "入网审核中"),

    ACCESS_SUCCESS(6, "入网成功"),

    ACCESS_FAIL(7, "入网失败"),

    CORPORATE_ACCOUNT_ANOMALY(8, "对公账户待验证或异常"),

    RISK_AUDIT(9, "风控审核中"),

    GENERATE_CONTRACT_SUCCESS(10, "短信签生成合同成功"),

    DATA_VALIDATION_FAIL(11, "资料验证失败"),

    ACCOUNT_SUSPENDED(12, "账户冻结");

    private Integer code;

    private String name;
}
