package com.tfjt.pay.external.unionpay.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 银联贷款进件请求参数
 */
@Data
@Builder
public class UnionPayLoansIncomingDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //	平台订单号;
    private String outRequestNo;
    //身份证信息
    private IdCardDTO idCard;
    //手机号码
    private String mobileNumber;
    //银行卡信息
    private SettleAcctDTO settleAcct;
    //	手机号验证码;
    private String smsCode;

}
