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

    private String tradeId;

    private String balanceAcctId;
    private String balanceAcctNo;
    private Integer amount;

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
