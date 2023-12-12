package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

/**
 * @Author zxy
 * @create 2023/12/9 16:32
 */
@Data
public class PabcBankNameAndCodeRespDTO {
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行code
     */
    private String bankCode;
}
