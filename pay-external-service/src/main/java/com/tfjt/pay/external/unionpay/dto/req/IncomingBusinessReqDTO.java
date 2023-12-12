package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
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
    @NotBlank(message = "进件id不能为空")
    private Long incomingId;

    /**
     * 营业执照信息id
     */
    private Long businessLicenseId;

    /**
     * 营业名称
     */
    @NotBlank(message = "营业名称不能为空")
    private String businessName;
    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String address;
    /**
     * 营业地区-省code
     */
    @NotBlank(message = "营业地区-省code不能为空")
    private String businessProvince;
    /**
     * 营业地区-省名称
     */
    @NotBlank(message = "营业地区-省名称不能为空")
    private String businessProvinceName;
    /**
     * 营业地区-市code
     */
    @NotBlank(message = "营业地区-市code不能为空")
    private String businessCity;
    /**
     * 营业地区-市名称
     */
    @NotBlank(message = "营业地区-市名称不能为空")
    private String businessCityName;
    /**
     * 营业地区-区code
     */
    @NotBlank(message = "营业地区-区code不能为空")
    private String businessDistrict;
    /**
     * 营业地区-区名称
     */
    @NotBlank(message = "营业地区-区名称不能为空")
    private String businessDistrictName;
    /**
     * 营业执照号码
     */
    @NotBlank(message = "营业执照号码不能为空")
    private String businessLicenseNo;
    /**
     * 营业执照照片
     */
    @NotBlank(message = "营业执照照片不能为空")
    private String businessLicenseUrl;
    /**
     * 营业执照有效起始日期
     */
    @NotBlank(message = "营业执照有效起始日期不能为空")
    private String businessLicenseEffectiveDate;
    /**
     * 营业执照有效截止日期
     */
    @NotBlank(message = "营业执照有效截止日期不能为空")
    private String businessLicenseExpireDate;
    /**
     * 营业执照是否长期，0否，1是
     */
    @NotBlank(message = "营业执照是否长期不能为空")
    private Integer businessLicenseIsLongTerm;

    /**
     * 联系邮箱
     */
    @NotBlank(message = "联系邮箱不能为空")
    private String email;


}
