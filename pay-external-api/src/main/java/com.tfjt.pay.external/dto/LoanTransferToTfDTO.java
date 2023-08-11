package com.tfjt.pay.external.dto;

import java.io.Serializable;

/**
 *
 * @author songx
 * @date 2023-08-11 10:54
 * @email 598482054@qq.com
 */
public class LoanTransferToTfDTO implements Serializable {
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

    public String getBalanceAcctId() {
        return balanceAcctId;
    }

    public void setBalanceAcctId(String balanceAcctId) {
        this.balanceAcctId = balanceAcctId;
    }

    public String getBalanceAcctName() {
        return balanceAcctName;
    }

    public void setBalanceAcctName(String balanceAcctName) {
        this.balanceAcctName = balanceAcctName;
    }

    public String getTfBalanceAcctId() {
        return tfBalanceAcctId;
    }

    public void setTfBalanceAcctId(String tfBalanceAcctId) {
        this.tfBalanceAcctId = tfBalanceAcctId;
    }

    public String getTfBalanceAcctName() {
        return tfBalanceAcctName;
    }

    public void setTfBalanceAcctName(String tfBalanceAcctName) {
        this.tfBalanceAcctName = tfBalanceAcctName;
    }
}
