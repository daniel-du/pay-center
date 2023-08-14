package com.tfjt.pay.external.unionpay.api.dto.resp;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-10 17:08
 * @email 598482054@qq.com
 */
public class UnionPayTransferDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 收款账户电子账簿id
     */
    private String inBalanceAcctId;

    /**
     * 收款账户电子账簿name
     */
    private String inBalanceAcctName;

    /**
     * 付款账户电子账簿id
     */
    private String outBalanceAcctId;

    /**
     * 付款账户电子账簿name
     */
    private String outBalanceAcctName;

    /**
     * amount
     */
    private Integer amount;
    /**
     * 交易订单号
     */
    private String tradeId;

    public String getInBalanceAcctId() {
        return inBalanceAcctId;
    }

    public void setInBalanceAcctId(String inBalanceAcctId) {
        this.inBalanceAcctId = inBalanceAcctId;
    }

    public String getInBalanceAcctName() {
        return inBalanceAcctName;
    }

    public void setInBalanceAcctName(String inBalanceAcctName) {
        this.inBalanceAcctName = inBalanceAcctName;
    }

    public String getOutBalanceAcctId() {
        return outBalanceAcctId;
    }

    public void setOutBalanceAcctId(String outBalanceAcctId) {
        this.outBalanceAcctId = outBalanceAcctId;
    }

    public String getOutBalanceAcctName() {
        return outBalanceAcctName;
    }

    public void setOutBalanceAcctName(String outBalanceAcctName) {
        this.outBalanceAcctName = outBalanceAcctName;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
}
