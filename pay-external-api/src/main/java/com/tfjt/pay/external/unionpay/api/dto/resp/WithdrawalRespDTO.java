package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName WithdrawalCreateReqDTO
 * @description: 提现创建
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Data
public class WithdrawalRespDTO implements Serializable {

    /**提现订单号*/
    private String withdrawalOrderNo;
    /**交易状态*/
    private String status;

    /**原因描述*/
    private String reason;


}
