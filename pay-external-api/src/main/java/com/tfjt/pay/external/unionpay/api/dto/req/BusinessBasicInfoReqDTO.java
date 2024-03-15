package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 21568
 * @create 2024/2/3 11:23
 */
@Data
public class BusinessBasicInfoReqDTO implements Serializable {
    private static final long serialVersionUID = 8207468149153308173L;

    private Long businessId;
    private Integer businessType;
}
