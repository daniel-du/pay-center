package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

/**
 * @description: ReqDeleteSettleAcctParams <br>
 * @date: 2023/5/23 15:41 <br>
 * @author: young <br>
 * @version: 1.0
 */
@Data
public class ReqDeleteSettleAcctParams {
    /**
     * 银行账号
     */
    private String bankAcctNo;
    /**
     * 个人用户ID
     */
    private String cusId;
    /**
     * 二级商户ID
     */
    private String mchId;
}
