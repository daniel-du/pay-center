package com.tfjt.pay.trade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用表
 *
 * @author ???
 * @email 598085205@qq.com
 * @date 2022-11-05 10:11:23
 */
@Data
@TableName("tf_pay_application")
public class PayApplicationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "id",type = IdType.AUTO)
	private Long id;
	/**
	 * 产品名称
	 */
	private String name;
	/**
	 * 第三方用户唯一凭证
	 */
	private String appId;
	/**
	 * 第三方用户唯一密钥
	 */
	private String appSecret;
	/**
	 * 公钥
	 */
	private String appPub;
	/**
	 * 私钥
	 */
	private String appPri;
	/**
	 * 负责人邮箱
	 */
	private String email;
	/**
	 * 负责人
	 */
	private String director;
	/**
	 * 创建人
	 */
	private Long creator;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 更新人
	 */
	private Long updater;
	/**
	 * 更新时间
	 */
	private Date updateDate;

}
