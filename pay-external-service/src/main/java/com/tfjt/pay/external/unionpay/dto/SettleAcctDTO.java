package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

@Data
    public class SettleAcctDTO implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 1-对私账户；
     * 2-对公账户
     */
    private String type;

    /**
     * 开户银行编码
     */
    private String bankCode;

    /**
     * 开户名称
     */
    private String name;

    /**
     * 开户银行省市编码
     */
    private String bankAddressCode;

    /**
     *开户银行联行号
     */
    private String bankBranchCode;

    /**
     * 银行帐号
     */
    private String bankAcctNo;

    /**
     * 职业
     */
    private String profession;
}
