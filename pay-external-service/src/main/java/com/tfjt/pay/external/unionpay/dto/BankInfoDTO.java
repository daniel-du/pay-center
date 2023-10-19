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

    /**
     * 结算类型 1个人 2企业
     */
    private Integer settlementType;

    /**
     * 打款验证状态
     */
    private Integer validateStatus;


}
