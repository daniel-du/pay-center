package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zxy
 * @create 2024/1/2 16:14
 */
@Data
public class MoudleStatusRespDTO implements Serializable {
    private static final long serialVersionUID = 8545175036366620333L;
    /**
     * 身份信息id
     */
    private Long cardId;
    /**
     * 营业信息ID
     */
    private Long merchantId;
    /**
     * 结算ID
     */
    private Long settleId;
}
