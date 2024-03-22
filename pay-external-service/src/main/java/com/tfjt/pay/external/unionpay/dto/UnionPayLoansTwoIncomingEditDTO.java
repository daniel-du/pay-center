package com.tfjt.pay.external.unionpay.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款二级进件请求参数
 */
@Data
@Builder
public class UnionPayLoansTwoIncomingEditDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //	二级商户ID;
    private String mchId;
    //	商户简称;
    private String shortName;
    //	平台订单号;
    private String outRequestNo;
    //	企业英文名称;
    private String englishName;

    //营业执照信
    private BusinessLicenseDTO businessLicense;
    //身份证信息
    private IdCardDTO legalPersonIdCard;
    //联系人
    private IdCardDTO contactIdCard;
    //实际控制企业信息
    private UnionPayLoansHoldingCompany holdingCompany;
    //银行卡信息
    private SettleAcctDTO settleAcct;


    //法人手机号
    private String legalPersonMobileNumber;
    //法人手机号
    private String contactMobileNumber;
    //法人邮箱
    private String contactEmail;
    //	手机号验证码;
    private String smsCode;
}
