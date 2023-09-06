package com.tfjt.pay.external.unionpay.dto;

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

    private String bankCode;//开户银行编码

    private String bankBranchCode;//开户银行联行号

    private String verifyStatus;

    private String bankAcctNo;//银行账号

    private String mobileNumber;//绑定的手机号

    private String requestId;

    private String bankAcctType;//银行账户类型

    private String name;//开户名称

    private String settleAcctId;

    private String acctValidationFailureMsg;

    private AcctValidationDTO acctValidation;//账户验证信息

}
