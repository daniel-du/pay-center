package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 21568
 * @create 2024/3/4 15:01
 */
@Data
public class ShopUpdateMqReqDTO implements Serializable {

    private static final long serialVersionUID = -5272374130900978411L;
    private Long shopId;
    private String beforeDistractCode;
    private String afterDistractCode;
    private String operator;
    private String operatorId;
}
