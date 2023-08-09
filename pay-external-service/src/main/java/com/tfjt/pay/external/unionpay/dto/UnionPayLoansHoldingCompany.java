package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnionPayLoansHoldingCompany implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;//实际控制企业名称
    private String licenseNumber;//实际控制企业营业执照号
    private String licenseValidTime;//实际控制企业营业期限
    private String licenseType;//实际控股人/企业证件类型1-营业执照2-其它

}
