package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 营业执照信
 */
@Data
public class BusinessLicenseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String copy;//营业执照扫描件

    private String number;//营业执照注册号

    private String companyName;//营业执照公司名称

    private String companyAddress;//营业执照注册地址

    private String validTime;//营业期限
}
