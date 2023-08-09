package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 进件失败原因
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-24 09:00:44
 */
@Data
@TableName("tf_ncoming_reason")
public class NcomingReasonEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 字段名称
	 */
	private String feildName;

	private String mappingFeildName;
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
	/**
	 * 贷款用户ID
	 */
	private Long loanUserId;
	/**
	 * 创建者
	 */
	private String creator;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 更新者
	 */
	private String updater;
	/**
	 * 更新时间
	 */
	private Date updateDate;

	/**
	 * 删除标记
	 */
	private Integer deleteFlag;

}
