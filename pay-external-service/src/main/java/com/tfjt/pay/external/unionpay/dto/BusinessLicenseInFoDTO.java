package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 营业执照信
 */
@Data
public class BusinessLicenseInFoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //营业执照扫描件
    private BusinessLicenseDTO businessLicenseDTO;
    //法人邮箱
    private String contactEmail;
}
