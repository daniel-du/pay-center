package com.tfjt.pay.external.unionpay.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 进件用户关键信息变更记录表
 *
 * @author zxy
 * @date 2023-09-07 09:58
 */
@Data
@TableName("tf_loanUser_keyInformation_changeRecord_log")
public class LoanUserKeyInformationChangeRecordLog implements Serializable {
    private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 用户id
	 */
	private Long loanUserId;
	/**
	 * 平台订单号
	 */
	private String outRequestNo;
	/**
	 * 二级商户系统订单号
	 */
	private String mchApplicationId;
	/**
	 * 绑定账户ID
	 */
	private String settleAcctId;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
