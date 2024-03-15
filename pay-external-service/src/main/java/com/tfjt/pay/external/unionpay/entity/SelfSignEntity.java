package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 入网表
 *
 * @author ???
 * @email 598085205@qq.com
 * @date 2022-11-09 11:57:33
 */
@Data
@TableName("tf_self_sign")
public class SelfSignEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId(value = "id",type = IdType.AUTO)
	private Long id;
	/**
	 * 接入平台id
	 */
	private String accesserId;
	/**
	 * 名称
	 */
	private String service;
	/**
	 * 请求时间
	 */
	private String requestDate;
	/**
	 * sign_type
	 */
	private String signType;
	/**
	 * 来源平台账户
	 */
	private String accesserAcct;
	/**
	 * 请求流水号
	 */
	private String requestSeq;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 第三方用户唯一凭证
	 */
	private String appId;

	/**
	 * 商户号
	 */
	private String mid;

	/**
	 * 企业用户号
	 */
	private String businessNo;

	private String signingStatus;

	/**
	 * 返回信息
	 */
	private String msg;

}
