package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 账户验证信息
 */
@Data
public class AcctValidationParamDTO  implements Serializable {
    private static final long serialVersionUID = 1L;
    //收款账户账号
    private String destAcctNo;
    //付款账户号
    private String acctNo;
    //收款账户户名
    private String destAcctName;
    //验证类型
    private String type;
    //付款户名
    private String acctName;
    //账户验证截止时间
    private Date deadline;
    //收款账户开户银行联行号
    private String destAcctBankBranchCode;
    //收款账户省市信息
    private String destAcctBankAddressCode;
    //收款账户备注信息
    private String memo;

}
