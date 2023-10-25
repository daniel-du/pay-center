package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.util.Map;

/**
 * @author tony
 * @version 1.0
 * @title DepositRespDTO
 * @description
 * @create 2023/10/19 13:49
 */
@Data
public class DepositReqDTO {

    /**
     * 平台的唯一请求单号
     */
    private String outOrderNo;

    /**
     * 发送时间
     */
    private String sentAt;

    /**
     * 总金额
     */
    private Long totalAmount;

    /**
     * 支付金额
     */
    private Long amount;

    /**
     * 平台优惠金额
     */
    private Long discountAmount;

    /**
     * 电子账簿ID
     */
    private String balanceAcctId;

    /**
     * 记账类型
     * {@link com.tfjt.pay.external.unionpay.enums.DepositTypeEnum}
     */
    private String depositType;

    /**
     * 支付类型
     */
    private String paymentType;

    /**
     * 支付通道订单号
     */
    private String paymentTradeNo;

    /**
     * 支付成功时间
     */
    private String paymentSucceededAt;

    /**
     * 备注
     */
    private String remark;

    /**
     * 扩展字段
     */
    private DepositExtraReqDTO extra;

    /**
     * 自定义参数
     */
    private Map<String,String> metadata;


}
