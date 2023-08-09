package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

@Data
public class LoanBalanceCreateDto {
    private Integer shopId;
    /**
     * 1 资管通 2福币
     */
    private Integer channelType;
    /**
     * 贷款服务开放状态 0不开放 1开放
     */
    private Integer openType;
    /**
     * 交易状态 0冻结 1正常
     */
    private Integer dealType;
    /**
     * 创建人

     */
    private String creator;

    /**
     * 修改人
     */
    private String updater;
}
