package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

/**
 * 下单收款商户信息
 * @author songx
 * @date 2023-08-15 14:47
 * @email 598482054@qq.com
 */
@Data
public class LoanOrderDetailsRespDTO {

    private static final long serialVersionUID = 1L;

    /**收款电子账簿id*/
    private String recvBalanceAcctId;
    /***
     * 业务子交易单号
     */
    private String subBusinessOrderNo;
    /***
     * 自定义参数 JSON
     */
    private String metadata;

    /**
     * 收款金额
     */
    private Integer amount;

}
