package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 回调日志表
 *
 * @author ???
 * @email 598085205@qq.com
 * @date 2022-11-05 10:11:22
 */
@Data
@TableName("tf_pay_callback_log")
public class PayCallbackLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "id",type = IdType.AUTO)
	private Long id;
	/**
	 * url
	 */
	private String url;
	/**
	 * 应用ID
	 */
	private String appId;
	/**
	 * 入参
	 */
	private String inParam;
	/**
	 * 出参
	 */
	private String outParam;
	/**
	 * 返回状态  1是异常
	 */
	private Integer status;
	/**
	 * 请求时间
	 */
	private Date requestTime;
	/**
	 * 响应时间

	 */
	private Date responseTime;
	/**
	 * 类型 1支付2分账3代付4入网5冻结
	 */
	private Integer type;
	/**
	 * 业务订单号
	 */
	private String merOrderId;

}
