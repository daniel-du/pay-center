package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tony
 * @version 1.0
 * @title ValidateStatusRespDTO
 * @description
 * @create 2023/10/23 11:00
 */
@Data
public class ValidateStatusRespDTO implements Serializable {

    private String settleAcctId;

    private boolean status;
}
