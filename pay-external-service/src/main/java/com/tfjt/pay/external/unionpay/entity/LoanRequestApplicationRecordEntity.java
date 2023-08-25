package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.PipedReader;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 贷款-调用回调业务日志表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-18 20:59:10
 */
@Data
@TableName("tf_loan_request_application_record")
public class LoanRequestApplicationRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * appid
	 */
	private String appId;
	/**
	 * 请求接口地址
	 */
	private String requestUrl;
	/**
	 * 请求参数
	 */
	private String requestParam;
	/**
	 * 响应地址
	 */
	private String responseParam;
	/**
	 * 接口响应时间
	 */
	private Integer responseTime;
	/**
	 * 交易单号
	 */
	private String tradeOrderNo;
	/**
	 * 请求时间
	 */
	private Date createTime;


	private Long callbackId;


	private Integer callbackStatus;
	/**
	 * 响应码
	 */
	private Integer responseCode;


	private String tradeType;

}
