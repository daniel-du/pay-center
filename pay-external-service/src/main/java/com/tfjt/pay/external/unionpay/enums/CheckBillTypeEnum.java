package com.tfjt.pay.external.unionpay.enums;

/**
 * 对账枚举类型
 * @author songx
 * @Date: 2023/10/28/11:44
 * @Description:
 */

public enum CheckBillTypeEnum {
    /**
     * 分账
     */
    DIVIDE_BALANCE("分账"),
    /**
     * 担保下单
     */
    LOAN_ORDER("担保下单"),
    /**
     * 担保确认
     */
    LOAN_ORDER_CONFIRM("担保确认"),

    /**
     * 提现
     */
    LOAN_WITHDRAWAL_ORDER("提现");

    /**
     * 对账类型
     */
    private String typeName;


    CheckBillTypeEnum(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}
