package com.tfjt.pay.external.unionpay.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tfjt.tfcommon.validator.group.AddGroup;
import com.tfjt.tfcommon.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 贷款-用户
 *
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-20 11:23:12
 */
@Data
@TableName("tf_loan_user")
public class LoanUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/**
	 * 类型1商家2供应商
	 */
	@NotNull(message = "类型不能为空",groups = {AddGroup.class})
	private Integer type;
	/**
	 * 业务ID
	 */
	@NotBlank(message = "业务ID不能为空",groups = {AddGroup.class})
	private String busId;

	@TableField(exist=false)
	private Integer supplierId;
	/**
	 * 枚举值：
checking：资料校验中
account_need_verify：待账户验证(四要素鉴权)
auditing：审核中
processing：处理中
signing:电子签约中
succeeded：已通过
failed：被驳回
	 */
	private String applicationStatus;
	/**
	 * 个人用户ID
	 */
	private String cusId;
	/**
	 * 二级商户ID
	 */
	private String mchId;
	/**
	 * 进件申请通过时间
	 */
	private Date succeededAt;

	/**
	 * 审核通过时间
	 */
	private Date auditedAt;
	/**
	 * 进件申请失败时间
	 */
	private Date failedAt;
	/**
	 * 审核失败原因
	 */
	private String failureMsgs;
	/**
	 * 审核失败参数
	 */
	private String failureMsgsParam;
	/**
	 * 审核失败驳回原因
	 */
	private String failureMsgsReason;
	/**
	 * 平台订单号
	 */
	@NotBlank(message = "平台订单号不能为空",groups = {UpdateGroup.class})
	private String outRequestNo;
	/**
	 * 系统订单号
	 */
	private Integer cusApplicationId;
	/**
	 * 创建人
	 */
	private String creator;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 更新人
	 */
	private String updater;
	/**
	 * 更新时间
	 */
	private Date updateDate;
	/**
	 * 商户简称
	 */
	private String name;

	/**
	 * 用户进件类型 0=个人进件 1-企业
	 * 2-个体工商户
	 */
	private Integer loanUserType;


	private String settleAcctId;

	private	String bindAcctName;

	private String mchApplicationId; //二级商户系统订单号


	/**
	 * 银行是否打款状态1是
	 */
	private Integer bankCallStatus;

}
