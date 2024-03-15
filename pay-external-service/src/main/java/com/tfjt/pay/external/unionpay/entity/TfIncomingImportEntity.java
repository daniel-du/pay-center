package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author Du Penglun
 * @since 2024-01-07
 */
@Getter
@Setter
@TableName("tf_incoming_import")
public class TfIncomingImportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 经销商/供应商/店铺id
     */
    private Long businessId;

    /**
     * 商铺类型（1：经销商/供应商 ，2：云商）
     */
    private Byte businessType;

    /**
     * 入网类型（1：贷款，2：商户入网）
     */
    private Byte accessType;

    /**
     * 入网主体类型（1：个体工商户，2：企业，3：小微）
     */
    private Byte accessMainType;

    /**
     * 入网渠道类型（1：平安，2：银联）
     */
    private Byte accessChannelType;

    /**
     * 商户简称
     */
    private String shopShortName;

    /**
     * 法人姓名
     */
    private String legalName;

    /**
     * 法人身份证号
     */
    private String legalIdNo;

    /**
     * 法人手机号
     */
    private String legalMobile;

    /**
     * 证件正面照片
     */
    private String legalFrontIdCardUrl;

    /**
     * 证件反面照片
     */
    private String legalBackIdCardUrl;



    /**
     * 证件有效起始日期
     */
    private String legalIdEffectiveDate;

    /**
     * 证件有效截止日期
     */
    private String legalIdExpiryDate;

    /**
     * 证件是否长期（0否，1是）
     */
    private Byte legalIdIsLongTerm;

    /**
     * 经办人姓名
     */
    private String agentName;

    /**
     * 经办人身份证号
     */
    private String agentIdNo;

    /**
     * 经办人手机号
     */
    private String agentMobile;

    /**
     * 营业名称
     */
    private String businessName;

    /**
     * 营业执照号码
     */
    private String businessLicenseNo;

    /**
     * 营业执照照片
     */
    private String businessLicenseUrl;

    /**
     * 营业地区-省code
     */
    private String businessProvince;

    /**
     * 营业地区-省名称
     */
    private String businessProvinceName;

    /**
     * 营业地区-市code
     */
    private String businessCity;

    /**
     * 营业地区-市名称
     */
    private String businessCityName;

    /**
     * 营业地区-区code
     */
    private String businessDistrict;

    /**
     * 营业地区-区名称
     */
    private String businessDistrictName;

    /**
     * 详细地址
     */
    private String address;


    /**
     * 营业执照有效起始日期
     */
    private String businessLicenseEffectiveDate;

    /**
     * 营业执照有效截止日期
     */
    private String businessLicenseExpireDate;

    /**
     * 营业执照是否长期（0否，1是）
     */
    private Boolean businessLicenseIsLongTerm;


    /**
     * 结算账户类型（1：对公结算，2：对私结算）
     */
    private Byte settleAccountType;

    /**
     * 银行开户名称
     */
    private String bankAccountName;

    /**
     * 银行卡号
     */
    private String bankCardNo;

    /**
     * 银行预留手机号
     */
    private String bankCardMobile;

    /**
     * 开户总行
     */
    private String bankName;

    /**
     * 联行号
     */
    private String bankBranchCode;

    /**
     * 开户支行
     */
    private String bankSubBranchName;

    /**
     * 数据提交状态（0：未提交，1：提交成功，2：提交失败）
     */
    private Byte submitStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
