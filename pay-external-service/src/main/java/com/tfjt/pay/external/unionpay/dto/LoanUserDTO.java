package com.tfjt.pay.external.unionpay.dto;


import lombok.Data;

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
public class LoanUserDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private Long id;

	/**
	 * 类型1商家2供应商
	 */
	private Integer type;
	/**
	 * 业务ID
	 */
	private String busId;
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
	 * 进件申请通过时间
	 */
	private Date succeededAt;
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
	private String outRequestNo;
	/**
	 * 系统订单号
	 */
	private Integer cusApplicationId;
	/**
	 * 创建人
	 */
	private Long creator;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 更新人
	 */
	private Long updater;
	/**
	 * 更新时间
	 */
	private Date updateDate;
	/**
	 * 商户简称
	 */
	private String name;

}
