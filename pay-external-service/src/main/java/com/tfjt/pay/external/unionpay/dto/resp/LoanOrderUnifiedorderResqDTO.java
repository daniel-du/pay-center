package com.tfjt.pay.external.unionpay.dto.resp;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author songx
 * @date 2023-08-19 09:32
 * @email 598482054@qq.com
 */
@Data
public class LoanOrderUnifiedorderResqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**唯一标识*/
    @JSONField(name = "out_trade_no")
    private String outTradeNo;

    /**付款账户信息*/
    @JSONField(name = "pay_balanceAcct_id")
    private String payBalanceAcctId;
    /**
     * 名称
     */
    @JSONField(name = "pay_balance_acct_name")
    private String payBalanceAcctName;

    /**自定义参数 JSON */
    private String metadata;

    /**收款电子账簿信息*/
    @JSONField(name = "details_dto_list")
    private List<LoanOrderDetailsRespDTO> detailsDTOList;

    /**
     * 1提现 2 转账 3 下单
     */
    @JSONField(name = "business_type")
    private Integer businessType;
    /**
     * 订单状态
     */
    @JSONField(name = "result_code")
    private String resultCode;

    /**
     * 交易金额
     */
    @JSONField(name = "total_fee")
    private Integer totalFee;

    /**
     * 银联订单号
     */
    @JSONField(name = "transaction_id")
    private String transactionId;


}
