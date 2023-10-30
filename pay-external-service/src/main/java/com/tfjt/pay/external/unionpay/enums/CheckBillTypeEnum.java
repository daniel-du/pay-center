package com.tfjt.pay.external.unionpay.enums;

import com.tfjt.pay.external.unionpay.service.LoanBalanceDivideService;
import com.tfjt.pay.external.unionpay.service.LoanOrderDetailsService;
import com.tfjt.pay.external.unionpay.service.LoanOrderService;
import com.tfjt.pay.external.unionpay.service.LoanWithdrawalOrderService;

/**
 * @Auther: songx
 * @Date: 2023/10/28/11:44
 * @Description:
 */

public enum CheckBillTypeEnum {

    DIVIDE_BALANCE("分账", LoanBalanceDivideService.class),

    LOAN_ORDER("担保下单", LoanOrderService.class),

    LOAN_ORDER_CONFIRM("担保确认", LoanOrderDetailsService.class),
    LOAN_WITHDRAWAL_ORDER("提现", LoanWithdrawalOrderService.class);
    //LOAN_WITHDRAWAL_ORDER("支付充值", LoanWithdrawalOrderEntity.class);

    /**
     * 对账类型
     */
    private String typeName;
    /**
     * 对账的实体类
     */
    private Class<?> clazz;

    CheckBillTypeEnum(String typeName, Class<?> clazz) {
        this.typeName = typeName;
        this.clazz = clazz;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}
