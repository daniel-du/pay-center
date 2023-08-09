package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 经营附加信息

 * 
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:39
 */
@Data
@TableName("tf_cust_business_attach_info")
public class CustBusinessAttachInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 
	 */
	private String imgUrl;
	/**
	 * {@link com.tfjt.pay.enums.ImgTypeEnum}
	 */
	private Integer type;
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
	 * 
	 */
	private Long custBusinessInfoId;

}
