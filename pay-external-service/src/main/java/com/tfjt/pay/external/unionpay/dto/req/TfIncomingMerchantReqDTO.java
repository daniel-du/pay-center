package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 9:43
 * @description 进件-保存商户身份信息入参
 */
@Data
public class TfIncomingMerchantReqDTO implements Serializable {

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
     * 经销商/供应商id/店铺id
     */
    private Long businessId;

    /**
     * 系统来源
     */
    private Byte businessType;

    /**
     * 入网渠道类型（1：平安，2：银联）
     */
    private Integer accessChannelType;

    /**
     * 入网类型（1：贷款，2：商户入网）
     */
    private Integer accessType;

    /**
     * 入网主体类型（1：个人，2：企业）
     */
    private Integer accessMainType;

    /**
     * 商户简称
     */
    private String shopShortName;

    /**
     * 法人身份证反面照片
     */
    private String legalBackIdCardUrl;
    /**
     * 法人身份证正面照片
     */
    private String legalFrontIdCardUrl;
    /**
     * 法人身份证手持照片
     */
    private String legalHoldIdCardUrl;
    /**
     * 法人身份证有效起始日期
     */
    private String legalIdEffectiveDate;
    /**
     * 法人身份证有效截止日期
     */
    private String legalIdExpiryDate;
    /**
     * 法人身份证是否长期，0否，1是
     */
    private long legalIdIsLongTerm;
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
     * 法人国籍
     */
    private String legalNationality;
    /**
     * 法人性别
     */
    private long legalSex;

    /**
     * 经办人身份证有效起始日期
     */
    private String agentIdEffectiveDate;
    /**
     * 经办人身份证有效截止日期
     */
    private String agentIdExpiryDate;
    /**
     * 经办人身份证是否长期，0否，1是
     */
    private String agentIdIsLongTerm;
    /**
     * 经办人证件号码
     */
    private String agentIdNo;
    /**
     * 经办人同法人，0否，1是
     */
    private long agentIsLegal;
    /**
     * 经办人手机
     */
    private String agentMobile;
    /**
     * 经办人姓名
     */
    private String agentName;


}
