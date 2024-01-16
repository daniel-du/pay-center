package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/13 9:43
 * @description 进件-提交银行基础信息
 */
@Data
public class IncomingSubmitMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户信息id
     */
    private Long id;

    /**
     * 进件id
     */
    private Long incomingId;


    /**
     * 入网主体类型（1：个体工商户，2：企业）
     */
    private Integer accessMainType;

    /**
     * 入网类型（1：贷款，2：商户入网）
     */
    private Integer accessType;

    /**
     * 入网渠道类型（1：平安，2：银联）
     */
    private Integer accessChannelType;

    /**
     * 入网状态
     */
    private Integer accessStatus;

    /**
     * 商户类型，1：经销商/供应商 ，2：云商
     */
    private Integer businessType;

    /**
     * 经销商/云商id
     */
    private Long businessId;

    /**
     * 子账户号
     */
    private String accountNo;

    /**
     * 商户简称
     */
    private String shopShortName;

    /**
     * 会员id
     */
    private String memberId;


    /**
     * 法人证件号码
     */
    private String legalIdNo;
    /**
     * 法人手机号
     */
    private String legalMobile;
    /**
     * 法人姓名
     */
    private String legalName;


    /**
     * 经办人证件号码
     */
    private String agentIdNo;

    /**
     * 经办人手机
     */
    private String agentMobile;
    /**
     * 经办人姓名
     */
    private String agentName;

    /**
     * 营业名称
     */
    private String businessName;

    /**
     * 营业执照号码
     */
    private String businessLicenseNo;

    /**
     * 营业执照类型
     */
    private String businessLicenseType;

    /**
     * 结算账户类型，1：对公，2：对私
     */
    private Integer settlementAccountType;

    /**
     * 开户名称
     */
    private String bankAccountName;
    /**
     * 联行号
     */
    private String bankBranchCode;
    /**
     * 银行预留手机号
     */
    private String bankCardMobile;
    /**
     * 银行卡号
     */
    private String bankCardNo;

    /**
     * 开户总行名称
     */
    private String bankName;

    /**
     * 开户总行编号
     */
    private String bankCode;
    /**
     * 开户支行名称
     */
    private String bankSubBranchName;
    /**
     * 超级网银号
     */
    private String eiconBankBranch;

    private Byte signChannel;
}
