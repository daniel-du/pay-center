package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ElectronicBookRespDTO
 * @description: 电子账簿查询返回结果
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Data
public class ElectronicBookResultRespDTO implements Serializable {
    /**流水ID*/
    private String balanceTxnId;

    /**电子账簿ID */
    private String balanceAcctId;

    /**系统订单号 */
    private Integer tradeId;

    /**请求单号*/
    private String outOrderNo;

    /**交易类型*/
    private Integer tradeType;

    /**业务类型*/
    private Integer txnType;

    /**可提现金额*/
    private Integer settledAmount;

    /**在途金额*/
    private Integer pendingAmount;

    /**不可用金额*/
    private Integer expensingAmount;

    /**备注*/
    private String remark;

    /**交易后可提现余额*/
    private Integer settledBalance;

    /**交易后在途余额*/
    private Integer pendingBalance;

    /**交易后不可用余额*/
    private Integer expensingBalance;

    /**原系统订单号*/
    private String origTradeId;

    /**原请求单号*/
    private String origOutOrderNo;

    /**交易成功时间*/
    private String succeededAt;
}
