package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tony
 * @version 1.0
 * @title BankCodeDTO
 * @description
 * @create 2023/10/20 11:47
 */

@Data
public class BankCodeRespDTO implements Serializable {

    /**
     * 银行号
     */
    private String bankCode;
    /**
     * 银行行联号
     */
    private String bankBranchCode;
    /**
     * 支行名称
     */
    private String bankName;

}
