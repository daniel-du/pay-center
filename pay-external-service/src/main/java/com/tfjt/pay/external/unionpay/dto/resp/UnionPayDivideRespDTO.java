package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 银联分账支付参数
 *
 * @author songx
 * @date 2023-08-14 14:30
 * @email 598482054@qq.com
 */
@Data
public class UnionPayDivideRespDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 分账订单系统订单号
     */
    private String allocationId;
    /**
     * 分账订单平台订单号
     */
    private String outOrderNo;
    /**
     * 付款电子账簿ID
     */
    private String payBalanceAcctId;
    /**
     * 分账明细
     */
    private List<UnionPayDivideRespDetailDTO> transferResults;
    /**
     * succeeded:成功
     * processing:处理中
     * failed:失败
     * partially_succeeded:部分成功
     */
    private String status;
    /**
     * 交易授权码
     */
    private String createdAt;
    /**
     * 处理完成时间
     */
    private String finishedAt;
    /**
     * 交易附言
     */
    private String remark;
    /**
     * 扩展字段
     */
    private String extra;
    /**
     * 自定义参数
     */
    private String metadata;
}
