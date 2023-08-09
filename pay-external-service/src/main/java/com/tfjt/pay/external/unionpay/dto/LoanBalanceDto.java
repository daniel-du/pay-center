package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

@Data
public class LoanBalanceDto {
    //贷款服务开放状态
    private String openType;
    //进件状态
    private String applicationStatus;
    //分账通道
    private String channelType;
    //商户类型
    private String loanUserType;
    //交易状态
    private String dealType;
}
