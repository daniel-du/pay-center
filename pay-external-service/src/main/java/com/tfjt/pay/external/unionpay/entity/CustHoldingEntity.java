package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

import static com.tfjt.pay.external.unionpay.constants.RegularConstants.*;

/**
 * 控股信息表
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-06-05 15:10:56
 */
@Data
@TableName("tf_cust_holding")
public class CustHoldingEntity implements Serializable {
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
	 * holding_type =1 实际控制企业名称   holding_type=2股东名称
	 */
	@NotBlank(message = "实际控制企业名称不能为空")
	@Length(min=1,max = 50)
	private String holdingName;
	/**
	 * holding_type = 1实际控制企业营业执照号   holding_type=2 身份证号
	 */
	@NotBlank(message = "营业执照号不能为空")
	@Pattern(regexp = LICENSE_PATTERN,message = "营业执照号码不正确")
	private String holdingNum;
	/**
	 * 1 企业 2 个人
	 */
	@NotBlank(message = "类型不能为空")
	private Integer holdingType;
	/**
	 * 生效日期
	 */
	private String effectiveDate;
	/**
	 * 失效日期
	 */
	private String expiryDate;
	/**
	 * 创建人
	 */
	private String creator;
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT-8")
	private Date createTime;
	/**
	 * 修改人
	 */
	private String updater;
	/**
	 * 修改时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT-8")
	private Date updateTime;

	/**
	 * 身份证是否长期 （0否，1是）
	 */
	private Integer isLongTerm;

}
