package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * payt服务通知记录表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-20 21:47:02
 */
@Data
@TableName("tf_loan_callback_application")
public class LoanCallbackApplicationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 通知应用id
	 */
	private String appId;
	/**
	 * 银联通知记录id
	 */
	private Long callbackId;
	/**
	 * 1 未通知  2 通知成功
	 */
	private Integer noticeStatus;
	/**
	 * 通知地址
	 */
	private String noticeUrl;
	/**
	 * 通知失败次数
	 */
	private Integer noticeErrorNumber;
	/**
	 * 发送参数
	 */
	private String requestParameter;
	/**
	 * 首次通知时间
	 */
	private Date createTime;
	/**
	 * 最后一次通知时间
	 */
	private Date updateTime;

}
