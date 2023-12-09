package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

/**
 * @Author zxy
 * @create 2023/12/9 16:32
 */
@Data
public class BankNameAndCodeRespDTO {
    /**
     * 银行名称
     */
    private String name;
    /**
     * 银行code
     */
    private String code;
}
