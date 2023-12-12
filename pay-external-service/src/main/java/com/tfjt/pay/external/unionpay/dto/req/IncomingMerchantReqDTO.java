package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 9:43
 * @description 进件-保存商户身份信息入参
 */
@Data
public class IncomingMerchantReqDTO implements Serializable {

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
    private Byte accessChannelType;

    /**
     * 入网类型（1：贷款，2：商户入网）
     */
    private Byte accessType;

    /**
     * 入网主体类型（1：个人，2：企业）
     */
    private Byte accessMainType;

    /**
     * 商户简称
     */
    @NotBlank(message = "进件id不能为空")
    private String shopShortName;

    /**
     * 法人身份证信息id
     */
    private Long legalIdCard;

    /**
     * 法人身份证反面照片
     */
    @NotBlank(message = "法人身份证反面照片不能为空")
    private String legalBackIdCardUrl;
    /**
     * 法人身份证正面照片
     */
    @NotBlank(message = "法人身份证正面照片不能为空")
    private String legalFrontIdCardUrl;
    /**
     * 法人身份证手持照片
     */
    @NotBlank(message = "法人身份证手持照片不能为空")
    private String legalHoldIdCardUrl;
    /**
     * 法人身份证有效起始日期
     */
    @NotBlank(message = "法人身份证有效起始日期不能为空")
    private String legalIdEffectiveDate;
    /**
     * 法人身份证有效截止日期
     */
    @NotBlank(message = "法人身份证有效截止日期不能为空")
    private String legalIdExpiryDate;
    /**
     * 法人身份证是否长期，0否，1是
     */
    @NotBlank(message = "法人身份证是否长期不能为空")
    private Byte legalIdIsLongTerm;
    /**
     * 法人证件号码
     */
    @NotBlank(message = "法人证件号码不能为空")
    private String legalIdNo;
    /**
     * 法人手机号
     */
    @NotBlank(message = "法人手机号不能为空")
    private String legalMobile;
    /**
     * 法人姓名
     */
    @NotBlank(message = "法人姓名不能为空")
    private String legalName;
    /**
     * 法人国籍
     */
    @NotBlank(message = "法人国籍不能为空")
    private String legalNationality;
    /**
     * 法人性别
     */
    @NotBlank(message = "法人性别不能为空")
    private Byte legalSex;

    /**
     * 经办人身份证信息id
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
    private Byte agentIdIsLongTerm;
    /**
     * 经办人证件号码
     */
    private String agentIdNo;
    /**
     * 经办人同法人，0否，1是
     */
    private Byte agentIsLegal;
    /**
     * 经办人手机
     */
    private String agentMobile;
    /**
     * 经办人姓名
     */
    private String agentName;


}
