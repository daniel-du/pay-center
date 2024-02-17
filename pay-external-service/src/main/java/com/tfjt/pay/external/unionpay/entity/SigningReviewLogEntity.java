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
	 *
	 */
	private String signData;
	/**
	 *
	 */
	private String jsonData;
	/**
	 *
	 */
	private String accesserId;
	/**
	 *
	 */
	private String env;
	/**
	 *
	 */
	private Date createTime;
	/**
	 *
	 */
	private Date updateTime;

}
