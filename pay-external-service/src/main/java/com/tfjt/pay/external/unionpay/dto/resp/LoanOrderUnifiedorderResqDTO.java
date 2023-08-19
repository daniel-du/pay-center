package com.tfjt.pay.external.unionpay.dto.resp;

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
    private String businessOrderNo;

    /**付款账户信息*/
    private String payBalanceAcctId;

    /**自定义参数 JSON */
    private String metadata;

    /**收款电子账簿信息*/
    private List<LoanOrderDetailsRespDTO> detailsDTOList;

    /**
     * 1提现 2 转账 3 下单
     */
    private Integer businessType;
}
