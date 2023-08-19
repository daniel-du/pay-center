package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-19 15:14
 * @email 598482054@qq.com
 */
@Data
public class LoanQueryOrderRespDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    /**唯一标识*/
    private String out_trade_no;

    /**付款账户信息*/
    private String pay_balanceAcct_id;
    /**
     * 名称
     */
    private String pay_balance_acct_name;

    /**自定义参数 JSON */
    private String metadata;

    /**收款电子账簿信息*/

    /**
     * 1提现 2 转账 3 下单
     */
    private Integer business_type;
    /**
     * 订单状态
     */
    private String result_code;

    /**
     * 交易金额
     */
    private Integer total_fee;

    /**
     * 银联订单号
     */
    private String transaction_id;
}
