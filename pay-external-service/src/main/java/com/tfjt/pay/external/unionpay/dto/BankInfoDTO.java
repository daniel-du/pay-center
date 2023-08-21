package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

@Data
public class BankInfoDTO {
    private Integer id;

    private String bankName;

    private String bankCardNo;

    /**
     * 总行
     */
    private String  bigBankName;

    /**
     * 开户名称
     */
    private String accountName;


}
