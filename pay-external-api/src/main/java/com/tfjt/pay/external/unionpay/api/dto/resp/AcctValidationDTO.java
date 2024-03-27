package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class AcctValidationDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //收款账户账号
    private String destAcctNo;
    //付款账户号
    private String acctNo;
    //收款账户户名
    private String destAcctName;

    /**
     * 1:平台用户向银行汇款指定金额
     * 2:银行向平台用户打款后由平台使用[打款金额验证]接口确认打款验证金额
     * 3:四要素鉴权(对私银行卡)
     */
    private String type;
    //付款户名
    private String acctName;
    //账户验证截止时间
    private String deadline;
    //收款账户开户银行联行号
    private String destAcctBankBranchCode;

}
