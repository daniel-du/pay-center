package com.tfjt.pay.external.unionpay.api.dto.resp;

import java.io.Serializable;

/**
 * @author tony
 * @version 1.0
 * @title CustBankInfoRespDTO
 * @description
 * @create 2023/8/17 11:26
 */
public class CustBankInfoRespDTO implements Serializable {
    private Integer id;

    /**
     * 支行
     */
    private String bankName;


    /**
     * 卡号
     */
    private String bankCardNo;

    /**
     * 总行
     */
    private String  bigBankName;

    /**
     * 开户名称
     */
    private String accountName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getBigBankName() {
        return bigBankName;
    }

    public void setBigBankName(String bigBankName) {
        this.bigBankName = bigBankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
