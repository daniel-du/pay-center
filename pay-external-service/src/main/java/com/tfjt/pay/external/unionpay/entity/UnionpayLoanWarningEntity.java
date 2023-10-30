package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 银联贷款交易明细报警表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-10-28 15:33:05
 */
@Data
@TableName("tf_unionpay_loan_warning")
public class UnionpayLoanWarningEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 交易类型
	 */
	private String type;
	/**
	 * 同福(子下单号/子退单id/转款补贴指令id)
	 */
	private String tfOrderNo;
	/**
	 * 银联(子下单号/子退单id/转款补贴指令id)
	 */
	private String subUnionpayOrderNo;
	/**
	 * 子商户号
	 */
	private String subMid;
	/**
	 * 金额
	 */
	private Long money;
	/**
	 * 是否处理0未处理 1已处理
	 */
	private Integer isOk;
	/**
	 * 原因
	 */
	private String cause;

	/**
	 * 报警批次号
	 */
	private String batchNo;
	/**
	 * 业务数据id
	 */
	private Long businessId;
	/**
	 * 业务数据表名
	 */
	private String tableName;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date updateTime;
	/**
	 * 预留字段1
	 */
	private String reservedOne;
	/**
	 * 预留字段2
	 */
	private String reservedTwo;
	/**
	 * 预留字段3
	 */
	private String reservedThree;

}
