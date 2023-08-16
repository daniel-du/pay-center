package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 贷款-订单-商品表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-08-16 16:42:20
 */
@Data
@TableName("tf_loan_order_goods")
public class LoanOrderGoodsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 关联收款信息id
	 */
	private Long detailsId;
	/**
	 * 订单编号(商品编号)
	 */
	private String orderBusinessOrderNo;
	/**
	 * 商品金额
	 */
	private Integer productAmount;
	/**
	 * 商品名称
	 */
	private String productName;
	/**
	 * 商品数量
	 */
	private Integer productCount;
	/**
	 * 创建时间
	 */
	private Date createAt;
	/**
	 * Appid
	 */
	private String appid;

}
