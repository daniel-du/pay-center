package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

import static com.tfjt.pay.external.unionpay.constants.RegularConstants.*;


/**
 * 营业信息表
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-06-05 15:06:04
 */
@Data
@TableName("fa_cust_business_detail")
public class CustBusinessDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 *
	 */
	private Long loanUserId;
	/**
	 * 营业执照
	 */
	@NotBlank(message="营业执照不能为空")
	private String businessImg;
	/**
	 * 营业名称
	 */
	@NotBlank(message="营业名称不能为空")
	@Length(max = 50)
	@Pattern(regexp = NO_SPECIAL_CHAR_PATTERN,message = "营业名称不支持符号类")
	private String businessName;
	/**
	 * 详细地址
	 */
	@NotBlank(message="详细地址不能为空")
	@Length(max=100,message = "长度不能超过100")
	private String businessAddress;
	/**
	 * 省
	 */
	@NotBlank(message="省不能为空")
	private String province;

	@NotBlank(message = "省名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String provinceName;
	/**
	 * 市
	 */
	@NotBlank(message="市不能为空")
	private String city;
	@NotBlank(message = "市名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String cityName;
	/**
	 * 区
	 */
	@NotBlank(message="区不能为空")
	private String district;
	@NotBlank(message = "区名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String districtName;
	/**
	 * 营业执照号码
	 */
	@NotBlank(message="营业执照号码不能为空")
	@Pattern(regexp = LICENSE_PATTERN,message = "营业执照号码不正确")
	private String businessNum;
	/**
	 * 生效日期
	 */
	private String effectiveDate;
	/**
	 * 失效日期
	 */
	private String expiryDate;


	@NotBlank(message = "邮箱不能为空")
	@Pattern(regexp = IDCARD_EMAIL,message = "邮箱格式不正确")
	private String email;
	/**
	 * 创建人
	 */
	private String creator;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改人
	 */
	private String updater;
	/**
	 * 银联图片ID
	 */
	private String businessImgMediaId;
	/**
	 * 修改人
	 */
	private Date updateTime;
	/**
	 * 身份证是否长期 （0否，1是）
	 */
	private Integer isLongTerm;

}
