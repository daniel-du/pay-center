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
 * 银行入网-身份信息
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Getter
@Setter
@TableName("tf_incoming_idcard_info")
public class TfIncomingIdcardInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 入网id
     */
    private Long incomingId;

    /**
     * 商户简称
     */
    private String shopShortName;

    /**
     * 法人身份证正面照片
     */
    private String legalFrontIdCardUrl;

    /**
     * 法人身份证反面照片
     */
    private String legalBackIdCardUrl;

    /**
     * 法人身份证手持照片
     */
    private String legalHoldIdCardUrl;

    /**
     * 法人姓名
     */
    private String legalName;

    /**
     * 法人性别
     */
    private Boolean legalSex;

    /**
     * 法人手机号
     */
    private String legalMobile;

    /**
     * 法人证件类型
     */
    private Boolean legalIdType;

    /**
     * 法人证件号码
     */
    private String legalIdNo;

    /**
     * 法人国籍
     */
    private String legalNationality;

    /**
     * 法人身份证有效起始日期
     */
    private String legalIdEffectiveDate;

    /**
     * 法人身份证有效截止日期
     */
    private String legalIdExpiryDate;

    /**
     * 法人身份证是否长期（0否，1是）
     */
    private Boolean legalIdIsLongTerm;

    /**
     * 经办人姓名
     */
    private String agentName;

    /**
     * 经办人手机
     */
    private String agentMobile;

    /**
     * 经办人证件类型
     */
    private Boolean agentIdType;

    /**
     * 经办人证件号码
     */
    private String agentIdNo;

    /**
     * 经办人身份证有效起始日期
     */
    private String agentIdEffectiveDate;

    /**
     * 经办人身份证有效截止日期
     */
    private String agentIdExpiryDate;

    /**
     * 经办人身份证是否长期（0否，1是）
     */
    private Boolean agentIdIsLongTerm;

    /**
     * 数据有效状态（0：有效，1：无效）
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
