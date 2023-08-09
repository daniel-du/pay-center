package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-06-08 09:06:25
 */
@Data
@TableName("tf_supplier_uuid")
public class SupplierUuidEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 供应商id
	 */
	private Integer supplierId;
	/**
	 * 供应商uuid
	 */
	private String supplierUuid;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 编辑时间
	 */
	private Date updateDate;

}
