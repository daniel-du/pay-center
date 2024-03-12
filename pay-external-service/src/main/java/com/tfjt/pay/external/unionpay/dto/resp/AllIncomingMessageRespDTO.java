package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/12 9:30
 * @description 供应商查询所有渠道入网状态、商户号信息 返参
 */
@Data
public class AllIncomingMessageRespDTO {

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 入网状态
     */
    private String accessStatusName;

    /**
     * 商户号
     */
    private String accountNo;

    /**
     * 企业号
     */
    private String accountBusinessNo;
}
