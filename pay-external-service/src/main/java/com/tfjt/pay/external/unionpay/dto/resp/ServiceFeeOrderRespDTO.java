package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ServiceFeeOrderRespDTO implements Serializable {

    /**
     * 付款电子账簿id
     */
    private String payBalanceAcctId;

    /**
     * 付款账户名称
     */
    private String payBalanceAcctName;

    /**
     * 银联系统唯一单号(银联返回)
     */
    private String combinedGuaranteePaymentId;

    /**
     * 服务费
     */
    private Integer productAmount;

    /**
     * 创建时间
     */
    private Date createAt;
}
