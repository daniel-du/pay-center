package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 联行号表
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-21 18:51:49
 */
@Data
@TableName("tf_bank_interbank_number")
public class BankInterbankNumberEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 开户银行联行号

	 */
	private String bankCode;
	/**
	 * 银行编码 
	 */
	private String drecCode;
	/**
	 * 城市编码
	 */
	private String cityCode;
	/**
	 * 支行名称
	 */
	private String bankBranchName;
	/**
	 * 状态  0=启用 1=废弃
	 */
	private Integer status;

}
