package com.tfjt.pay.external.unionpay.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 贷款用户电子账单
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-23 08:48:02
 */
@Data
@TableName("tf_loan_balance_acct")
public class LoanBalanceAcctEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 贷款用户主键
	 */
	private Integer loanUserId;
	/**
	 * 电子账簿ID
	 */
	private String balanceAcctId;
	/**
	 * 电子账簿账簿号
	 */
	private String relAcctNo;
	/**
	 * 电子账号名称
	 */
	private String balanceAcctName;

}
