package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 *
 * @author young
 * @email blank.lee@hotmail.com
 * @date 2024-02-07 09:24:34
 */
@Data
@TableName("tf_signing_review_log")
@Accessors(chain = true)
public class SigningReviewLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 对json_data明文内容做签名摘要
	 */
	private String signData;
	/**
	 * 对json_data明文内容做签名摘要
	 */
	private String jsonData;
	/**
	 * 外部系统平台标识(明文)，由ums分配
	 */
	private String accesserId;
	/**
	 * 环境
	 */
	private String env;
	/**
	 * 解密后的数据
	 */
	private String data;
	/**
	 *
	 */
	private Date createTime;
	/**
	 *
	 */
	private Date updateTime;



}
