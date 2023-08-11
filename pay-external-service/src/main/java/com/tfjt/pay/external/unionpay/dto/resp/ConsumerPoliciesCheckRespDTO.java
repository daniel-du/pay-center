package com.tfjt.pay.external.unionpay.dto.resp;

import com.tfjt.pay.external.unionpay.dto.req.ExtraDTO;
import com.tfjt.pay.external.unionpay.dto.req.GuaranteePaymentDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ConsumerPoliciesRespDTO
 * @description: 合并消费担保确认返回结果
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Data
public class ConsumerPoliciesCheckRespDTO implements Serializable {
    /**
     * 合并消费担保下单系统订单号
     */
    private String combinedGuaranteeConfirmId;
    /**
     * 合并消费担保下单子订单系统订单号
     */
    private String guaranteePaymentId;

    /**
     * 平台订单号
     */
    private String outOrderNo;

    /**
     * 交易状态
     */
    private String status;

    /**
     * 失败原因
     */
    private String reason;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 完成时间
     */
    private String finishedAt;

    /**
     * 确认交易金额
     */
    private String amount;

    /**
     * 已退款金额
     */
    private String refundedAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 扩展字段
     */
    private List<ExtraDTO> extra;

    /**
     * 自定义参数
     */
    private Map<String,Object> metadata;

    /**
     * 分账ID
     */
    private String allocationId;
}
