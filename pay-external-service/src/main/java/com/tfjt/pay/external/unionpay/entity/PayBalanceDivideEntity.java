package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 分账记录表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-14 16:05:56
 */
@Data
@TableName("tf_pay_balance_divide")
public class PayBalanceDivideEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 主交易单号
	 */
	private String tradeOrderNo;
	/**
	 * 付款账号id
	 */
	private String payBalanceAcctId;
	/**
	 * 备注信息
	 */
	private String remark;
	/**
	 * 
	 */
	private String metadata;
	/**
	 * 分账订单系统订单号
	 */
	private String allocationId;
	/**
	 * succeeded:成功
processing:处理中
failed:失败
partially_succeeded:部分成功

	 */
	private String status;
	/**
	 * 创建时间
	 */
	private Date createAt;
	/**
	 * 处理完成时间
	 */
	private Date finishedAt;
	/**
	 * 分账业务系统标识

	 */
	private String businessSystemId;
	/**
	 * 分账订单号不能为空
	 */
	private String businessOrderNo;

}
