package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 营业执照信
 */
@Data
public class BusinessLicenseInFoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private BusinessLicenseDTO businessLicenseDTO;//营业执照扫描件
    private String contactEmail;//法人邮箱
}
