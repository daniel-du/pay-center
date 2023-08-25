package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 贷款订单商户收款信息表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 16:42:20
 */
@Data
@TableName("tf_loan_order_details")
public class LoanOrderDetailsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 贷款订单id
	 */
	private Long orderId;
	/**
	 * 金额
	 */
	private Integer amount;
	/**
	 * 收款用户的电子账簿ID
	 */
	private String recvBalanceAcctId;

	/**
	 * 收款用户的电子账簿
	 */
	private String recvBalanceAcctName;

	/**
	 * 付款电子账簿id
	 */
	private String payBalanceAcctId;
	/**
	 * 备注信息
	 */
	private String remark;
	/**
	 * 业务子交易订单号()
	 */
	private String subBusinessOrderNo;
	/**
	 * 子单号订单号(pay系统生产)
	 */
	private String tradeOrderNo;
	/**
	 * 银联系统生产子单号唯一值
	 */
	private String guaranteePaymentId;
	/**
	 * 已退款金额
	 */
	private Integer refundedAmount;
	/**
	 * 已确认金额
	 */
	private Integer confirmedAmount;
	/**
	 * Succeeded:成功 processing:处理中 failed:失败

	 */
	private String status;
	/**
	 * 失败原因
	 */
	private String reason;
	/**
	 * 创建时间
	 */
	private Date createdAt;
	/**
	 * 完成时间
	 */
	private Date finishedAt;
	/**
	 * 自定义参数
	 */
	private String metadata;
	/**
	 * 子业务系统id
	 */
	private String appId;

	private Long payLoanUserId;

	private Long recvLoanUserId;

}
