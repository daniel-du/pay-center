package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

/**
 * @author tony
 * @version 1.0
 * @title ParentBalanceRespDTO
 * @description
 * @create 2023/10/18 09:49
 */

@Data
public class ParentBalanceRespDTO {
    private String accountNo;
    private String accountId;
    private String bankCardNo;
    private Integer amount;
}
