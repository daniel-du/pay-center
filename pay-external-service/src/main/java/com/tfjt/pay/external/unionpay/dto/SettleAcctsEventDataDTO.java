package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 事件内容
 */
@Data
public class SettleAcctsEventDataDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //绑定账户编号
    private String settleAcctId;
    //平台订单号
    private String outRequestNo;
    //账户验证信息
    private AcctValidationParamDTO acctValidationParam;

}
