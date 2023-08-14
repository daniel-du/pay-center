package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * 银联分账明细
 * @author songx
 * @date 2023-08-14 14:35
 * @email 598482054@qq.com
 */
@Data
public class UnionPayDivideRespDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**分账订单子订单系统订单号*/
    private String transferId;

    /**分账批次系统订单号*/
    private String allocationId;

    /**分账订单平台订单号*/
    private String outOrderNo;

    /**付款电子账簿ID*/
    private String payBalanceAcctId;

    /**收款电子账簿ID*/
    private String recvBalanceAcctId;

    /**金额*/
    private Integer amount;

    /**交易状态*/
    private String status;

    /**失败原因*/
    private String reason;

    /**创建时间*/
    private String createdAt;

    /**完成时间*/
    private String finishedAt;

    /**附言*/
    private String remark;


}
