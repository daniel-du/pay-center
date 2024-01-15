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
     * 进件id
     */
    private Long incomingId;

    /**
     * 入网状态
     */
    private Byte accessStatus;

    /**
     * 入网主体类型
     */
    private Byte accessMainType;

    /**
     * 入网渠道类型
     */
    private Byte accessChannelType;

    /**
     * 银行卡预留手机号
     */
    private String bankCardMobile;

    /**
     * 身份信息id
     */
    private Long merchantId;
    /**
     * 营业信息ID
     */
    private Long businessId;
    /**
     * 结算ID
     */
    private Long settleId;
}
