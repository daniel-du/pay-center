package com.tfjt.pay.external.unionpay.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 联行号表-城市代码
 * 
 * @author effine
 * @email iballad@163.com
 * @date 2023-05-21 18:51:49
 */
@Data
@TableName("tf_bank_area")
public class BankAreaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	/**
	 * 省
	 */
	private String name;
	/**
	 * 省 code
	 */
	private String code;
	/**
	 * 市
	 */
	private String pid;
}
