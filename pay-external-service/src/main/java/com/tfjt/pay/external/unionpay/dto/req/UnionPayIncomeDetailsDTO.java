package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-14 22:16
 * @email 598482054@qq.com
 */
@Data
public class UnionPayIncomeDetailsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 交易订单号
     */
    private String tradeId;
    /**
     * 收款账号id
     */
    private String balanceAcctId;
    /**
     *
     */
    private String balanceAcctNo;
    /**
     * 收款金额
     */
    private Integer amount;
    /***
     * 付款账号
     */
    private String payBankAcctNo;

    private String payBankAcctName;
    private String payBankCode;
    private String payBankBranchCode;
    private String bankMemo;
    private String recordedAt;
    private String transactionNo;
    private String tradeType;
    private String origTradeId;
    private String origOutOrderNo;


}
