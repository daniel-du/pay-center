package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 应用表-回调
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-18 20:59:16
 */
@Data
@TableName("tf_pay_application_callback_url")
public class PayApplicationCallbackUrlEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 回调url
	 */
	private String url;
	/**
	 * 应用ID
	 */
	private String appId;
	/**
	 * 类型 1支付2分账3代付4划付5退票6入网签约7退款回调 8 补贴 
	 */
	private Integer type;
	/**
	 * 创建时间
	 */
	private Date createDate;

}
