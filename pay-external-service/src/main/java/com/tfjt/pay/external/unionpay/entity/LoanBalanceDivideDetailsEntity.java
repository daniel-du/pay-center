package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 分账详情表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-14 16:05:56
 */
@Data
@TableName("tf_loan_balance_divide_details")
public class LoanBalanceDivideDetailsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 分账id
	 */
	private Long divideId;
	/**
	 * 收款电子账簿ID
	 */
	private String recvBalanceAcctId;
	/**
	 * 分账金额
	 */
	private Integer amount;
	/**
	 * 子交易单号
	 */
	private String subTradeOrderNo;
	/**
	 * 收款户名
	 */
	private String recvBalanceAcctName;
	/**
	 * 交易状态
	 */
	private String status;
	/**
	 * 失败原因
	 */
	private String reason;
	/**
	 * 银联备注
	 */
	private String remark;
	/**
	 * 附言
	 */
	private String createRemark;
	/**
	 * 处理完成时间
	 */
	private Date finishedAt;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建人id
	 */
	private Long userId;
	/**
	 * 1  银联交易中 2 交易成功 3 交易失败 4 结果未知
	 */
	private Integer state;
	/**
	 * 业务系统子项订单号
	 */
	private String subBusinessOrderNo;
	/**
	 * */
	private Long loanUserId;

}
