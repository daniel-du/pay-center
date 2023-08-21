package com.tfjt.pay.external.unionpay.api.dto.req;

import java.io.Serializable;

/**
 * @title WithdrawalReqDTO
 * @description
 * @author tony
 * @version 1.0
 * @create 2023/8/16 11:21
 */

public class WithdrawalReqDTO implements Serializable {
    private Long loanUserId;
    private Integer amount;

    private Long bankInfoId;

    private String appId;

    private String version;

    public Long getLoanUserId() {
        return loanUserId;
    }

    public void setLoanUserId(Long loanUserId) {
        this.loanUserId = loanUserId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getBankInfoId() {
        return bankInfoId;
    }

    public void setBankInfoId(Long bankInfoId) {
        this.bankInfoId = bankInfoId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
