package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 入金通知
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-12 16:21:00
 */
@Data
@TableName("tf_pay_banlance_notice")
public class PayBalanceNoticeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 通知事件类型：
			transfer_deposit：转账入金
			root_transfer_deposit：母户入金
			withdrawal_return:提现退汇
			large_payment-大额订单
			transfer_deposit_refund_return：虚户入金退款退汇
			large_payment_deposit_refund_return：大额订单退款退汇
	 */
	private String eventType;
	/**
	 * 事件id
	 */
	private String eventId;
	/**
	 * 交易订单号
	 */
	private String tradeId;
	/**
	 * 入金电子账簿ID
	 */
	private String balanceAcctId;
	/**
	 * 关联银行账号
	 */
	private String balanceAcctNo;
	/**
	 * 交易金额
	 */
	private Integer amount;
	/**
	 * 转出方银行账号
	 */
	private String payBankAcctNo;
	/**
	 * 转出方银行账户名称
	 */
	private String payBankAcctName;
	/**
	 * 转出方银行编号
	 */
	private String payBankCode;
	/**
	 * 转出方银行联行号
	 */
	private String payBankBranchCode;
	/**
	 * 银行附言
	 */
	private String bankMemo;
	/**
	 * 记账时间
	 */
	private Date recordedAt;
	/**
	 * 交易流水号
	 */
	private String transactionNo;
	/**
	 * 交易类型10：母户入金
11：转账入金
13：大额订单入金
33：提现退汇
37：虚户入金退回退汇
39：大额订单入金退回退汇

	 */
	private String tradeType;
	/**
	 * 原交易系统订单号
	 */
	private String origTradeId;
	/**
	 * 原交易平台订单号
	 */
	private String origOutOrderNo;
	/**
	 * 事件创建时间
	 */
	private Date createdAt;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
