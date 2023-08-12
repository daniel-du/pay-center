package com.tfjt.pay.external.unionpay.dto.resp;

import com.tfjt.pay.external.unionpay.dto.req.GuaranteePaymentDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ConsumerPoliciesRespDTO
 * @description: 合并消费担保下单返回结果
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Data
public class ConsumerPoliciesRespDTO implements Serializable {
    /**
     * 合并消费担保下单系统订单号
     */
    private String combinedGuaranteePaymentId;

    /**
     * 平台订单号
     */
    private String combinedOutOrderNo;

    /**
     * 付款电子账簿ID
     */
    private String payBalanceAcctId;

    /**
     * 担保消费明细
     */
    private List<GuaranteePaymentDTO> guaranteePaymentResults;

    /**
     * 交易状态
     */
    private String status;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 处理完成时间
     */
    private String finishedAt;

    /**
     * 备注
     */
    private String remark;

    /**
     * 扩展字段
     */
    private Map<String,Object> extra;

    /**
     * 自定义参数
     */
    private Map<String,Object> metadata;
}
