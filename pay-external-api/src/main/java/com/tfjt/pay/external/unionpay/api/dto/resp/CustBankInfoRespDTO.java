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

    private String bankName;

    private String bankCarNo;

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

    public String getBankCarNo() {
        return bankCarNo;
    }

    public void setBankCarNo(String bankCarNo) {
        this.bankCarNo = bankCarNo;
    }
}
