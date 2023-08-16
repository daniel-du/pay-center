package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 银联通知记录表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 17:14:28
 */
@Data
@TableName("tf_loan_notice_record")
public class LoanNoticeRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 事件id
	 */
	private String eventId;
	/**
	 * 系统订单号
	 */
	private String tradeId;
	/**
	 * pay系统订单号
	 */
	private String orderNo;
	/**
	 * 创建时间
	 */
	private Date createAt;
	/**
	 * 处理完成时间
	 */
	private Date finishedAt;
	/**
	 * succeeded:成功;failed:失败;partially_succeeded:部分成功

	 */
	private String status;
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
