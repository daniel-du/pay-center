package com.tfjt.pay.external.unionpay.dto.message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/1/5 8:49
 * @description 进件完成消息实体
 */
@Data
@Builder
public class IncomingFinishDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 进件id
     */
    private Long id;

    /**
     * 入网主体类型（1：个体工商户，2：企业）
     */
    private Integer accessMainType;

    /**
     * 入网渠道类型（1：平安，2：银联）
     */
    private Integer accessChannelType;

    /**
     * 子账户号
     */
    private String accountNo;

    /**
     * 商户类型，1：经销商/供应商 ，2：云商
     */
    private Integer businessType;

    /**
     * 商户id
     */
    private Long businessId;


}
