package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * 银联分账明细
 * @author songx
 * @date 2023-08-14 14:35
 * @email 598482054@qq.com
 */
@Data
public class UnionPayDivideRespReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**收款电子账簿ID*/
    private String recvBalanceAcctId;

    /**分账金额*/
    private Integer amount;

    /**备注*/
    private String remark;


}
