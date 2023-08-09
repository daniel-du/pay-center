package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 贷款余额表
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-06-06 16:02:09
 */
@Data
@TableName("tf_loan_balance")
public class LoanBalanceEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 
	 */
	private Integer shopId;
	/**
	 * 1 资管通 2福币
	 */
	private Integer channelType;
	/**
	 * 贷款服务开放状态 0不开放 1开放
	 */
	private Integer openType;
	/**
	 * 交易状态 0冻结 1正常
	 */
	private Integer dealType;
	/**
	 * 创建人

	 */
	private String creator;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 修改人
	 */
	private String updater;
	/**
	 * 修改时间
	 */
	private Date updateDate;

}
