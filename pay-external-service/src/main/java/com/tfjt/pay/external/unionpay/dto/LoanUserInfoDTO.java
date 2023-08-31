package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

@Data
public class LoanUserInfoDTO {
    private String merchantShortName;

    /**
     * 1=小微商户 2=二级商户
     */
    private Integer type;

    private String applicationStatus;

    private String errMsg;

    /**
     * 个人用户ID
     */
    private String cusId;
    private String phone;

    /**
     * 用户进件类型 0=个人进件 1-企业
     * 2-个体工商户
     */
    private Integer loanUserType;

    private String mchApplicationId; //二级商户系统订单号

    /**
     * 平台订单号
     */
    private String outRequestNo;
    private String mchId;


}
