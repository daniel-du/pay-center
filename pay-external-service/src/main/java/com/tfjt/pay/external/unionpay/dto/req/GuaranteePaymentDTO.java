package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @title GuaranteePaymentDTO
 * @description 担保消费明细参数
 * @author A1583
 * @version 1.0
 * @Date 2023/8/9 11:10
 */
@Data
public class GuaranteePaymentDTO implements Serializable {
    /**金额*/
    private Integer amount;

    /**收款电子账簿ID*/
    private String recvBalanceAcctId;

    /**备注*/
    private String remark;

    /**平台子订单号*/
    private String outOrderNo;

    /**扩展字段*/
    private Map<String,Object> extra;

    /**自定义参数*/
    private Map<String,Object> metadata;

    /**已退款金额*/
    private Integer refundedAmount;

    /**已确认金额*/
    private Integer confirmedAmount;

    /**合并消费担保下单子订单系统订单号*/
    private String guaranteePaymentId;

    /**付款电子账簿ID*/
    private String payBalanceAcctId;

    /**交易状态*/
    private String status;

    /**失败原因*/
    private String reason;

    /**创建时间*/
    private String createdAt;

    /**处理完成时间*/
    private String finishedAt;

}
