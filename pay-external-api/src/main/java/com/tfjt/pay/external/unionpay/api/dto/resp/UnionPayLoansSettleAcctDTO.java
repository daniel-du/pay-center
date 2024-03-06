package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: YinLianLoansSettleAcctDTO <br>
 * @date: 2023/5/23 11:24 <br>
 * @author: young <br>
 * @version: 1.0
 */
@Data
public class UnionPayLoansSettleAcctDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String outRequestNo;
    //开户银行编码
    private String bankCode;
    //开户银行联行号
    private String bankBranchCode;

    private String verifyStatus;
    //银行账号
    private String bankAcctNo;
    //绑定的手机号
    private String mobileNumber;

    private String requestId;
    //银行账户类型
    private String bankAcctType;
    //开户名称
    private String name;

    private String settleAcctId;

    private String acctValidationFailureMsg;
    //账户验证信息
    private AcctValidationDTO acctValidation;

}
