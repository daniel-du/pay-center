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
	 * 事件类型
	 */
	private String eventType;
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


}
