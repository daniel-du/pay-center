package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 贷款-进件-回调表
 *
 * @author chenshun
 * @email lixiaolei
 * @date 2023-06-06 14:26:37
 */
@Data
@TableName("tf_loan_callback")
public class LoanCallbackEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 *
	 */
	private Long loanUserId;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 事件ID
	 */
	private String eventId;
	/**
	 * 事件类型
	 */
	private String eventType;
	/**
	 * 事件创建时间
	 */
	private String createdAt;
	/**
	 * 事件内容
	 */
	private String eventData;
	//类型1 打款验证 2 二级进件
	private Integer type;
	//收款账户账号
	private String destAcctNo;

	/**
	 * 关联业务表id
	 */
	private Long businessId;


	/**
	 * 银联交易单号
	 */
	private String treadOrderNo;

	/**
	 * 0 需要通知 1 待通知 2 已通知
	 */
	private Integer noticeStatus;

}
