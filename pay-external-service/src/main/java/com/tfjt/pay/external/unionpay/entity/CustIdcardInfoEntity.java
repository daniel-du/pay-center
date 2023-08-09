package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import static com.tfjt.pay.external.unionpay.constants.RegularConstants.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * 身份信息表
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:39
 */
@Data
@TableName("tf_cust_idcard_info")
public class CustIdcardInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 商户简称
	 */
	@NotBlank(message="商户简称不能为空")
	@Length(min=1,max = 20)
	@Pattern(regexp = CHARACTER_CHECK,message = "商户简称支持中文、字母、数字、括号和&符号")
	private String merchantShortName;
	/**
	 * 身份证正面
	 */
	@NotBlank(message="身份证正面不能为空")
	private String frontIdCardUrl;
	/**
	 * 身份证反面
	 */
	@NotBlank(message="身份证反面不能为空")
	private String backIdCardUrl;
	/**
	 * 手持身份证
	 */
	@NotBlank(message="手持身份证不能为空")
	private String holdIdCardUrl;
	/**
	 * 姓名
	 */
	@NotBlank(message="姓名不能为空")
	@Length(min=1,max = 20)
	private String name;

	/**
	 * 性别
	 * 1：男 2：女
	 */
	@NotNull(message="性别不能为空")
	private Integer sex;
	/**
	 * 身份证号码
	 */
	@NotBlank(message="身份证号码不能为空")
	@Pattern(regexp =IDCARD_CHECK ,message = "身份证号格式不正确")
	private String idNo;
	/**
	 * 生效日期
	 */
	private String effectiveDate;
	/**
	 * 失效日期
	 */
	private String expiryDate;
	/**
	 * 国籍 1：中国 2：其它国家或地区
	 */
	@NotBlank(message="国籍不能为空")
	private String nationality;

	/**
	 *
	 */
	@NotNull(message="贷款用户ID不能为空")
	private Long loanUserId;
	/**
	 * 创建者
	 */
	private String creator;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 更新者
	 */
	private String updater;
	/**
	 * 更新时间
	 */
	private Date updateDate;

	/**
	 * 身份证正面
	 */
	@NotBlank(message="身份证正面不能为空-银联")
	private String frontIdCardUrlMediaId;
	/**
	 * 身份证反面
	 */
	@NotBlank(message="身份证反面不能为空-银联")
	private String backIdCardUrlMediaId;
	/**
	 * 身份证是否长期 （0否，1是）
	 */
	private Integer isLongTerm;

}
