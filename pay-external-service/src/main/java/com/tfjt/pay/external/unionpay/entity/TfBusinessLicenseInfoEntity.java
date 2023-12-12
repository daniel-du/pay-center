package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 营业执照信息
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
@Data
@Builder
@NoArgsConstructor
@TableName("tf_business_license_info")
public class TfBusinessLicenseInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 营业执照照片
     */
    private String businessLicenseUrl;

    /**
     * 营业名称
     */
    private String businessName;

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
     * 营业执照号码
     */
    private String businessLicenseNo;

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
     * 数据有效状态（0：有效，1：无效）
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
