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
 * @description 进件-保存商户营业信息入参
 */
@Data
public class IncomingBusinessReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 营业信息id
     */
    @NotNull(message = "商户身份id不能为空", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 进件id
     */
    @NotNull(message = "进件id不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private Long incomingId;

    /**
     * 营业执照信息id
     */
    @NotNull(message = "营业执照id不能为空", groups = {UpdateGroup.class})
    private Long businessLicenseId;

    /**
     * 营业名称
     */
    @NotBlank(message = "营业名称不能为空", groups = { AddGroup.class, UpdateGroup.class })
    @Length(min=1,max = 50)
    private String businessName;
    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空", groups = { AddGroup.class, UpdateGroup.class })
    @Length(min=1,max = 100, message ="详细地址最长为100字符", groups = { AddGroup.class, UpdateGroup.class })
    private String address;
    /**
     * 营业地区-省code
     */
    @NotBlank(message = "营业地区-省code不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String businessProvince;
    /**
     * 营业地区-省名称
     */
    @NotBlank(message = "营业地区-省名称不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String businessProvinceName;
    /**
     * 营业地区-市code
     */
    @NotBlank(message = "营业地区-市code不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String businessCity;
    /**
     * 营业地区-市名称
     */
    @NotBlank(message = "营业地区-市名称不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String businessCityName;
    /**
     * 营业地区-区code
     */
    @NotBlank(message = "营业地区-区code不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String businessDistrict;
    /**
     * 营业地区-区名称
     */
    @NotBlank(message = "营业地区-区名称不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String businessDistrictName;
    /**
     * 营业执照号码
     */
    @NotBlank(message = "营业执照号码不能为空", groups = { AddGroup.class, UpdateGroup.class })
    @Pattern(regexp = RegularConstants.SOCIAL_CREDIT_CODE, message = "营业执照号码格式错误", groups = { AddGroup.class, UpdateGroup.class })
    @Length(min=18,max = 18, message ="详细地址最长为100字符", groups = { AddGroup.class, UpdateGroup.class })
    private String businessLicenseNo;
    /**
     * 营业执照照片
     */
    @NotBlank(message = "营业执照照片不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String businessLicenseUrl;
    /**
     * 营业执照有效起始日期
     */
    @NotBlank(message = "营业执照有效起始日期不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String businessLicenseEffectiveDate;
    /**
     * 营业执照有效截止日期
     */
    @NotBlank(message = "营业执照有效截止日期不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private String businessLicenseExpireDate;
    /**
     * 营业执照是否长期，0否，1是
     */
    @NotNull(message = "营业执照是否长期不能为空", groups = { AddGroup.class, UpdateGroup.class })
    private Integer businessLicenseIsLongTerm;

    /**
     * 联系邮箱
     */
    private String email;


}
