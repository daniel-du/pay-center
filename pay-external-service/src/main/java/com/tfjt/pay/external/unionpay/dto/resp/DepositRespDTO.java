package com.tfjt.pay.external.unionpay.dto.resp;

import com.tfjt.pay.external.unionpay.dto.req.DepositExtraReqDTO;
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
public class DepositRespDTO {

    /**
     * 系统订单号
     */
    private String depositId;
    /**
     * 交易状态
     * succeeded:成功
     * processing:处理中
     * failed:失败
     */
    private String status;

    /**
     * 入账状态
     * wait_credit:等待入账
     * credited:已入账
     * none_credited:无需入账
     */
    private String creditStatus;

    /**
     * 失败原因
     */
    private String reason;

    /**
     * 创建时间
     */
    private String createAt;

    /**
     * 处理完成时间
     */
    private String finishedAt;

    /**
     * 平台订单号
     */
    private String outOrderNo;

    /**
     * 总金额
     */
    private int totalAmount;

    /**
     * 支付金额
     */
    private int amount;

    /**
     * 平台优惠金额
     */
    private int discountAmount;

    /**
     * 电子账簿ID
     */
    private String balanceAcctId;

    /**
     * 支付类型
     */
    private String paymentType;

    /**
     * 已退款金额
     */
    private Integer refundedAmount;

    /**
     * 已退款营销金额
     */
    private Integer refundedDiscountAmount;

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
