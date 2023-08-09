package com.tfjt.pay.external.unionpay.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款二级进件请求参数
 */
@Data
@Builder
public class UnionPayLoansTwoIncomingDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String outRequestNo;//	平台订单号;
    private String organizationType;//	主体类型;
    private String shortName;//	商户简称;
    private String englishName;//	企业英文名称;

    private BusinessLicenseDTO businessLicense;//营业执照信
    private IdCardDTO legalPersonIdCard;//法人身份证信息
    private IdCardDTO contactIdCard;//联系人
    private UnionPayLoansHoldingCompany holdingCompany;//实际控制企业信息
    private SettleAcctDTO settleAcct;//银行卡信息

    private String legalPersonMobileNumber;//法人手机号
    private String contactMobileNumber;//法人手机号

    private String contactEmail;//法人邮箱
    private String smsCode;//	手机号验证码;


}
