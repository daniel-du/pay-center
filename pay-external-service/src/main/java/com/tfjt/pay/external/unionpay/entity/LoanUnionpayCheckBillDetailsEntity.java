package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 贷款-银联对账记录表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-10-28 11:17:26
 */
@Data
@TableName("tf_loan_unionpay_check_bill_details")
public class LoanUnionpayCheckBillDetailsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 平台id
	 */
	private String platformId;
	/**
	 * 平台名称
	 */
	private String platformName;
	/**
	 * 银行名称
	 */
	private String bankName;
	/**
	 * 银行机构号
	 */
	private String bankOrgNo;
	/**
	 * 账单日期
	 */
	private Date billDate;
	/**
	 * 平台订单号
	 */
	private String platformOrderNo;
	/**
	 * 系统订单后
	 */
	private String systemOrderNo;
	/**
	 * 交易类型
	 */
	private String treadType;
	/**
	 * 
	 */
	private String treadStatus;
	/**
	 * 交易发起时间
	 */
	private Date treadCreateTime;
	/**
	 * 交易完成时间
	 */
	private Date treadCompeleteTime;
	/**
	 * 发起方名称
	 */
	private String initiatorName;
	/**
	 * 发起方账号
	 */
	private String initiatorAccount;
	/**
	 * 收款方名称
	 */
	private String payeeName;
	/**
	 * 收款方账号
	 */
	private String payeeAccount;
	/**
	 * 订单金额
	 */
	private BigDecimal orderMoney;
	/**
	 * 手续费
	 */
	private BigDecimal platformFeeMoney;
	/**
	 * 是否核对 0 未核对 1 已核对
	 */
	private Integer checkStatus;

}
