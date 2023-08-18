package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author songx
 * @date 2023-08-11 10:54
 * @email 598482054@qq.com
 */
@Data
public class LoanTransferToTfReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 入金电子账簿ID
     */
    private String balanceAcctId;
    /**
     * 银行账号
     */
    private String balanceAcctName;
    /**
     * 同福电子行号
     */
    private String tfBalanceAcctId;
    /**
     * 同福电子账簿id
     */
    private String tfBalanceAcctName;
}
