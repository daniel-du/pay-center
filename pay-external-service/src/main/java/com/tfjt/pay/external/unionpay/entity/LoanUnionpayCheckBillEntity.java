package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 贷款银联对账表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-18 20:59:10
 */
@Data
@TableName("tf_loan_unionpay_check_bill")
public class LoanUnionpayCheckBillEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 对账日期
	 */
	private Date date;
	/**
	 * 七牛url
	 */
	private String url;
	/**
	 * 母账户id
	 */
	private String balanceAcctId;
	/**
	 * 创建时间
	 */
	private Date ceateTime;


	private Integer status;


	private String reason;

}
