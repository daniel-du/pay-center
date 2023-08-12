package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName WithdrawalCreateReqDTO
 * @description: 提现创建
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Data
public class WithdrawalCreateRespDTO implements Serializable {
    /**系统订单号*/
    private String withdrawalId;

    /**平台订单号 */
    private String outOrderNo;

    /**交易状态*/
    private String status;

    /**原因描述*/
    private String reason;

    /**创建时间*/
    private String createdAt;

    /**
     * 完成时间
     */
    private String finishedAt;
    /**
     * 金额
     */
    private Integer amount;
    /**
     * 平台手续费
     */
    private Integer serviceFee;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 电子账簿ID
     */
    private String balanceAcctId;

    /**
     * 银行附言
     */
    private String bankMemo;

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
    private String metadata;

}
