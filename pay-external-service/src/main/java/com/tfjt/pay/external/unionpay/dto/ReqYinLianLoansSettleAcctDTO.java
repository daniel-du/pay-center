package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.util.Map;

/**
 * @description: ReqYinLianLoansSettleAcctDTO <br>
 * @date: 2023/5/23 11:24 <br>
 * @author: young <br>
 * @version: 1.0
 */
@Data
public class ReqYinLianLoansSettleAcctDTO {

    /**
     * 平台订单号
     */
    private String outRequestNo;
    /**
     * 个人用户ID
     */
    private String cusId;
    /**
     * 二级商户ID
     */
    private String mchId;
    /**
     * 银行账户类型
     */
    private String bankAcctType;
    /**
     * 开户银行编码
     */
    private String bankCode;
    /**
     * 开户银行省市编码
     */
    private String bankAddressCode;
    /**
     * 开户银行联行号
     */
    private String bankBranchCode;
    /**
     * 银行账号
     */
    private String bankAcctNo;
    /**
     * 职业
     */
    private String profession;

    /**
     * 绑定的手机号
     */
    private String mobileNumber;
    /**
     * 短信验证码
     */
    private String smsCode;

    private Map metadata;

    private Map extra;


}
