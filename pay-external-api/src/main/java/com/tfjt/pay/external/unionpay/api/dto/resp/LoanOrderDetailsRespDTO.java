package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-28 17:28
 * @email 598482054@qq.com
 */
@Data
public class LoanOrderDetailsRespDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 收款电子账簿id
     */
    private String recv_balance_acct_id;

    /**
     * 收款方名称
     */

    private String recv_balance_acct_name;
    /***
     * 业务子交易单号
     */

    private String sub_business_order_no;
    /***
     * 自定义参数 JSON
     */

    private String metadata;

    /**
     * 收款金额
     */
    private Integer amount;
}
