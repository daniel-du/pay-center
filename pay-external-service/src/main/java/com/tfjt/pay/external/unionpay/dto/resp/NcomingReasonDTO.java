package com.tfjt.pay.external.unionpay.dto.resp;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 进件失败原因
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-24 09:00:44
 */
@Data
public class NcomingReasonDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	/**
	 * 字段名称
	 */
	private String feildName;
	/**
	 * 原因
	 */
	private String faildReason;
	/**
	 * 状态
1成功 0失败
	 */
	private Integer status;
	/**
	 * 类型
1 - 身份信息
2 - 结算信息
3 - 图片信息
	 */
	private Integer type;

}
