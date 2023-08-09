package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AcctValidationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String destAcctNo;//收款账户账号

    private String acctNo;//付款账户号

    private String destAcctName;//收款账户户名

    /**
     * 1:平台用户向银行汇款指定金额
     * 2:银行向平台用户打款后由平台使用[打款金额验证]接口确认打款验证金额
     * 3:四要素鉴权(对私银行卡)
     */
    private String type;

    private String acctName;//付款户名

    private String deadline;//账户验证截止时间

    private String destAcctBankBranchCode;//收款账户开户银行联行号

}
