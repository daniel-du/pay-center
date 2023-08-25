package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 贷款订单表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 16:42:20
 */
@Data
@TableName("tf_loan_order")
public class LoanOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 银联交易唯一单号(pay服务生成)
	 */
	private String tradeOrderNo;
	/**
	 * 业务交易单号(业务系统生成)
	 */
	private String businessOrderNo;
	/**
	 * 付款电子账簿id
	 */
	private String payBalanceAcctId;
	/**
	 * 备注信息
	 */
	private String remark;
	/**
	 * 自定义参数
	 */
	private String metadata;
	/**
	 * 银联系统唯一单号(银联返回)
	 */
	private String combinedGuaranteePaymentId;
	/**
	 * 创建时间
	 */
	private Date createAt;
	/**
	 * 处理完成时间
	 */
	private Date finishedAt;
	/**
	 * 交易状态  succeeded:成功 processing:处理中 failed:失败partially_succeeded:部分成功

	 */
	private String status;
	/**
	 * 调用系统appid
	 */
	private String appId;
	/**
	 * 1提现 2 转账 3 下单
	 */
	private Integer businessType;

	/**
	 * 付款账户名称
	 */
	private String payBalanceAcctName;

	private Integer amount;

	private Integer confirmStatus;
	/**
	 * 用户id
	 */
	private Long loanUserId;

}
