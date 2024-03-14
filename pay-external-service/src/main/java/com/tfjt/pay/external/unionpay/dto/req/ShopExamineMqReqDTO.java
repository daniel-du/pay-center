package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 21568
 * @create 2024/3/4 14:59
 */
@Data
public class ShopExamineMqReqDTO implements Serializable {
    private static final long serialVersionUID = 4461507012005801717L;
    private Long shopId;
    private String shopName;
    private String phone;
    private String beforeDistractCode;
    private String afterDistractCode;
    private String operatorName;
    private String operatorId;
}
