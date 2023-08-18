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

	private Integer type;//类型1 打款验证 2 二级进件

	private String destAcctNo;//收款账户账号


	/**
	 * 20:支付充值;
	 21:充值退款;
	 25:可提现支付充值;
	 30:单笔提现/批量提现申请;
	 40:消费;
	 41:消费退款;
	 51:分账;
	 52:合并支付充值;
	 53:批量提现确认;
	 60:担保下单;
	 62:担保未确认退款;
	 63:担保已确认退款;
	 64:担保支付;
	 66:担保支付退款;
	 67:担保支付确认退款;
	 73:资金调拨;
	 74:提前入账

	 */
	private Integer tradeType;
	/**
	 * 0 需要通知 1 待通知 2 已通知
	 */
	private Integer noticeStatus;
	/**
	 * 通知失败次数
	 */
	private Integer noticeErrorNumber;
	/**
	 * 关联业务表id
	 */
	private Long businessId;
	/**
	 * 回调业务系统地址
	 */
	private String noticeUrl;
	/**
	 * 业务appid
	 */
	private String appid;

}
