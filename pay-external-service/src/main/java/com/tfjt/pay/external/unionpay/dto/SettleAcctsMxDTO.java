package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SettleAcctsMxDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String outRequestNo;
    private String bankCode;

    private String bankBranchCode;

    private String acctValidationFinishedAt;

    private String verifyStatus;

    private String bankAcctNo;

    private String bankAcctType;

    private String name;

    private String bankAddressCode;

    private String settleAcctId;
}
