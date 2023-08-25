package com.tfjt.pay.trade.dto.req;

import com.tfjt.tfcommon.core.validator.group.AddGroup;
import com.tfjt.tfcommon.core.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
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
public class PayApplicationEntityDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	private Long id;
	/**
	 * 产品名称
	 */
	@NotBlank(message = "公司名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
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
