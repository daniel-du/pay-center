package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/1 16:05
 * @description
 */
@Data
public class QueryIncomingSettleByMerchantReqDTO {

    /**
     * 商户id
     */
    @NotNull(message = "商户id不能为空")
    private Integer businessId;

    /**
     * 商户类型 1：经销商、供应商 2、云商
     */
    @NotNull(message = "商户类型不能为空")
    private Integer businessType;

    /**
     * 入网渠道类型 1：平安 2、银联
     */
    @NotNull(message = "入网渠道类型不能为空")
    private Integer accessChannelType;
}
