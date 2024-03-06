package com.tfjt.pay.external.unionpay.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 9:43
 * @description 进件-查询商户身份信息出参
 */
@Data
public class IncomingMerchantRespDTO implements Serializable {

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
     * 入网主体类型（1：个人，2：企业）
     */
    private Byte accessMainType;

    /**
     * 商户简称
     */
    private String shopShortName;

    /**
     * 法人身份证信息id
     */
    private Long legalIdCard;
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
    private Integer legalIdIsLongTerm;
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
    private Byte legalSex;

    /**
     * 经办人证件信息
     */
    private Long agentIdCard;

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
    private Integer agentIdIsLongTerm;
    /**
     * 经办人证件号码
     */
    private String agentIdNo;

    /**
     * 经办人身份证反面照片
     */
    private String agentBackIdCardUrl;
    /**
     * 经办人身份证正面照片
     */
    private String agentFrontIdCardUrl;
    /**
     * 经办人同法人，0否，1是
     */
//    private Byte agentIsLegal;
    /**
     * 经办人手机
     */
    private String agentMobile;
    /**
     * 经办人姓名
     */
    private String agentName;


}
