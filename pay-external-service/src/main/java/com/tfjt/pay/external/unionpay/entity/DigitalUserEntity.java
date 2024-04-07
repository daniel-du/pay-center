package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 数字人民币开通信息表
 * 
 * @author songx
 * @email 598482054@163.com
 * @date 2023-11-28 17:03:59
 */
@Data
@TableName("tf_digital_user")
public class DigitalUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;


	/**
	 * 验证类型
	 */
	@TableField(exist = false)
	private String verifyType;
	/**
	 * 账户
	 */
	private String mchntSideAccount;
	/**
	 * 签约协议号
	 */
	private String signContract;
	/**
	 * 钱包id识别码
	 */
	private String walletId;
	/**
	 * 运营机构id
	 */
	private String operatorId;
	/**
	 * 运营机构名称
	 */
	private String operatorName;
	/**
	 * 运营机构图标
	 */
	private String operatorIcon;
	/**
	 * 协议信息
	 */
	private String protocollInfo;
	/**
	 * 0 解绑  1 正常 
	 */
	private Integer status;
	/**
	 * 解绑时间
	 */
	private Date unbindTime;
	/**
	 * 绑定时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
