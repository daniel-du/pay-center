package com.tfjt.pay.external.unionpay.api.dto.req;

import com.tfjt.pay.external.unionpay.api.dto.UserTypeDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @title WithdrawalReqDTO
 * @description
 * @author tony
 * @version 1.0
 * @create 2023/8/16 11:21
 */

@Data
public class WithdrawalReqDTO extends UserTypeDTO implements Serializable {
    private Integer amount;

    private Long bankInfoId;

    private String appId;


}
