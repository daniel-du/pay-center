package com.tfjt.pay.external.unionpay.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款进件请求参数
 */
@Data
@Builder
public class UnionPayLoansIncomingEditDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cusId;//	个人用户ID;
    private String smsCode;//	手机号验证码;
    private String outRequestNo;//	平台订单号;
    private IdCardDTO idCard;//身份证信息
    private String mobileNumber;//手机号码
    private SettleAcctDTO settleAcct;//银行卡信息
}
