package com.tfjt.pay.external.unionpay.dto;

import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import static com.tfjt.pay.external.unionpay.constants.RegularConstants.*;

@Data
public class CustBusinessCreateDto {

    @NotNull(message = "用户id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Long loanUserId;
    @NotBlank(message = "营业执照不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String businessImg;
    /**
     * 营业名称
     */
    @NotBlank(message = "营业名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(min=1,max = 50)
    @Pattern(regexp = NO_SPECIAL_CHAR_PATTERN,message = "营业名称不支持符号类")
    private String businessName;
    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max=100,message = "长度不能超过100")
    private String businessAddress;
    /**
     * 省
     */
    @NotBlank(message = "省不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String province;
    @NotBlank(message = "省名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String provinceName;
    /**
     * 市
     */
    @NotBlank(message = "市不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String city;
    @NotBlank(message = "市名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String cityName;
    /**
     * 区
     */
    @NotBlank(message = "区不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String district;
    @NotBlank(message = "区名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String districtName;
    /**
     * 营业执照号码
     */
    @NotBlank(message = "营业执照编码不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Pattern(regexp = LICENSE_PATTERN,message = "营业执照号码不正确")
    private String businessNum;

    @NotBlank(message = "邮箱为空", groups = {AddGroup.class, UpdateGroup.class})
    @Pattern(regexp = IDCARD_EMAIL,message = "邮箱格式不正确")
    private String email;
    /**
     * 生效日期
     */
    private String effectiveDate;
    /**
     * 失效日期
     */
    private String expiryDate;

    private String creator;

    private String updater;

    /**
     * 身份证是否长期 （0否，1是）
     */
    private Integer isLongTerm;

    @NotBlank(message = "营业执照图片地址不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String businessImgMediaId;

    private String imgUrl;

    private String type;

}
