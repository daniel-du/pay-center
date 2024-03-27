package com.tfjt.pay.external.unionpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 21568
 * @create 2024/2/6 13:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessIsIncomingRespDTO {
    private String accountNo;
    private Long businessId;
    private Byte businessType;
}
