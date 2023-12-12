package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2023/12/11 9:43
 * @description 进件-保存商户营业信息入参
 */
@Data
public class IncomingBusinessReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 营业信息id
     */
    private Long id;

    /**
     * 进件id
     */
    private Long incomingId;

    /**
     * 营业名称
     */
    private String businessName;
    /**
     * 详细地址
     */
    private String address;
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
     * 营业执照号码
     */
    private String businessLicenseNo;
    /**
     * 营业执照照片
     */
    private String businessLicenseUrl;
    /**
     * 营业执照有效起始日期
     */
    private String businessLicenseEffectiveDate;
    /**
     * 营业执照有效截止日期
     */
    private String businessLicenseExpireDate;
    /**
     * 营业执照是否长期，0否，1是
     */
    private Integer businessLicenseIsLongTerm;

    /**
     * 联系邮箱
     */
    private String email;


}
