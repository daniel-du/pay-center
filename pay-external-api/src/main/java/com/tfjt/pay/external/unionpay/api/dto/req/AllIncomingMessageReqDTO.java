package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/12 8:48
 * @description 供应商查询所有渠道入网状态、商户号信息 入参
 */
@Data
public class AllIncomingMessageReqDTO implements Serializable {

    /**
     * 商户类型, 1：供应商、经销商  2：云商
     */
    private Integer businessType;

    /**
     * 商户id
     */
    private Long businessId;
}
