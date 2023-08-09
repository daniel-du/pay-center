package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 银联-贷款-日志表
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-23 13:48:30
 */
@Data
@TableName("tf_union_pay_loan_req_log")
public class UnionPayLoanReqLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 请求参数
	 */
	private String reqParams;
	/**
	 * 交易码
	 */
	private String transCode;
	/**
	 * 版本号
	 */
	private String verNo;
	/**
	 * 请求系统日期
	 */
	private String srcReqDate;
	/**
	 * 请求系统时间
	 */
	private String srcReqTime;
	/**
	 * 请求系统流水号
	 */
	private String srcReqId;
	/**
	 * 渠道号
	 */
	private String channelId;
	/**
	 * 集团号

	 */
	private String groupId;
	/**
	 * 交易渠道

	 */
	private String lwzBussCode;
	/**
	 * 资管通业务信息

	 */
	private String lwzData;
	/**
	 * 资管通业务类型

	 */
	private String lwzChannelType;
	/**
	 * 签名

	 */
	private String signature;
	/**
	 * 贷款用户ID
	 */
	private Long loanUserId;
	/**
	 * 请求时间
	 */
	private Date requestTime;
	/**
	 * 响应时间
	 */
	private Date responseTime;

	/**
	 * 请求返回结果
	 */
	private String result;

}
