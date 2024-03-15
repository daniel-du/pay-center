package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

/**
 * @Author 21568
 * @create 2024/2/6 13:40
 */
@Data
public class BusinessIsIncomingRespDTO {
    private String accountNo;
    private Long businessId;
    private Integer businessType;
}
