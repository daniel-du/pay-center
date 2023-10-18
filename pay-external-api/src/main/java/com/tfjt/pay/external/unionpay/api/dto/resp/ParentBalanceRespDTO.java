package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author tony
 * @version 1.0
 * @title ParentBalanceRespDTO
 * @description
 * @create 2023/10/18 09:49
 */

@Data
public class ParentBalanceRespDTO implements Serializable {
    private String accountNo;
    private String accountId;
    private String bankCardNo;
    private BigDecimal amount;
}
