package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 事件内容
 */
@Data
public class SettleAcctsEventDataDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String settleAcctId;//绑定账户编号
    private String outRequestNo;//平台订单号
    private AcctValidationParamDTO acctValidationParam; //账户验证信息

}
