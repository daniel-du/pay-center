package com.tfjt.pay.external.unionpay.dto.req;

import com.tfjt.pay.external.unionpay.constants.RegularConstants;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
     * 商户身份信息id
     */
    @NotNull(message = "商户身份id不能为空", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 进件id
     */
    @NotNull(message = "进件主表id不能为空", groups = {UpdateGroup.class, AddGroup.class})
    private Long incomingId;

    /**
     * 经销商/供应商id/店铺id
     */
//    @NotNull(message = "商户id不能为空", groups = {AddGroup.class})
//    private Long businessId;
//
//    /**
//     * 系统来源
//     */
//    @NotNull(message = "系统来源不能为空", groups = {AddGroup.class})
//    private Byte businessType;
//
//    /**
//     * 入网渠道类型（1：平安，2：银联）
//     */
//    @NotNull(message = "入网渠道类型不能为空", groups = {AddGroup.class})
//    private Byte accessChannelType;
//
//    /**
//     * 入网类型（1：贷款，2：商户入网）
//     */
//    @NotNull(message = "入网类型不能为空", groups = {AddGroup.class})
//    private Byte accessType;
//
    /**
     * 入网主体类型（1：个人，2：企业）
     */
    private Byte accessMainType;

    /**
     * 商户简称
     */
    @NotBlank(message = "商户简称不能为空")
    private String shopShortName;

    /**
     * 法人身份证信息id
     */
    @NotNull(message = "法人身份id不能为空", groups = {UpdateGroup.class})
    private Long legalIdCard;

    /**
     * 法人身份证反面照片
     */
    @NotBlank(message = "法人身份证背面照片不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String legalBackIdCardUrl;
    /**
     * 法人身份证正面照片
     */
    @NotBlank(message = "法人身份证正面照片不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String legalFrontIdCardUrl;
    /**
     * 法人身份证手持照片
     */
//    @NotBlank(message = "法人身份证手持照片不能为空", groups = { AddGroup.class, UpdateGroup.class })
//    private String legalHoldIdCardUrl;
    /**
     * 法人身份证有效起始日期
     */
    @NotBlank(message = "法人身份证有效起始日期不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String legalIdEffectiveDate;
    /**
     * 法人身份证有效截止日期
     */
    @NotBlank(message = "法人身份证有效截止日期不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String legalIdExpiryDate;
    /**
     * 法人身份证是否长期，0否，1是
     */
    @NotNull(message = "法人身份证是否长期不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private Byte legalIdIsLongTerm;
    /**
     * 法人证件号码
     */
    @NotBlank(message = "法人证件号码不能为空", groups = { AddGroup.class, UpdateGroup.class })
    @Length(min=15,max = 18, groups = { AddGroup.class, UpdateGroup.class })
    private String legalIdNo;
    /**
     * 法人手机号
     */
    @NotBlank(message = "法人手机号不能为空", groups = { AddGroup.class, UpdateGroup.class })
    @Pattern(regexp = RegularConstants.MOBILE, message = "法人手机号格式错误", groups = { AddGroup.class, UpdateGroup.class })
    @Length(min=11,max = 11, groups = { AddGroup.class, UpdateGroup.class })
    private String legalMobile;
    /**
     * 法人姓名
     */
    @NotBlank(message = "法人姓名不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String legalName;
    /**
     * 法人国籍
     */
//    @NotBlank(message = "法人国籍不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String legalNationality;
    /**
     * 法人性别
     */
    @NotNull(message = "法人性别不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private Byte legalSex;

    /**
     * 经办人身份证信息id
     */
    private Long agentIdCard;

    /**
     * 经办人身份证反面照片
     */
    private String agentBackIdCardUrl;
    /**
     * 经办人身份证正面照片
     */
    private String agentFrontIdCardUrl;

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

    /**
     * 签约渠道，1：APP，2：平台h5网页，3：公众号，4：小程序
     */
//    private Byte signChannel;


}
