package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/25 15:28
 * @description 查询进件信息出参
 */
@Data
public class IncomingMessageRespDTO implements Serializable {

    /**
     * 进件id
     */
    private Long id;

    /**
     * 商户id
     */
    private Long businessId;

    /**
     * 商户类型, 1：供应商、经销商  2：云商
     */
    private Integer businessType;

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
     * 子账户号
     */
    private String accountNo;

    /**
     * 会员id
     */
    private String memberId;

    /**
     * 会员名称
     */
    private String memberName;
    /**
     * 入网状态（1：信息填写，2：入网中，3：入网成功，4：入网失败）
     */
    private Integer accessStatus;


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
     * 营业名称
     */
    private String businessName;

    /**
     * 营业执照号码
     */
    private String businessLicenseNo;

    /**
     * 结算账户类型，1：对公，2：对私
     */
    private Integer settlementAccountType;

    /**
     * 开户名称
     */
    private String bankAccountName;
    /**
     * 银行预留手机号
     */
    private String bankCardMobile;
    /**
     * 银行卡号
     */
    private String bankCardNo;
}
