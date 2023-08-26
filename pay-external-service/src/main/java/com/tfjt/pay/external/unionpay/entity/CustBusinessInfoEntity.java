package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 经营信息
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:38
 */
@Data
@TableName("tf_cust_business_info")
public class CustBusinessInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 是否有固定营业场所 0 无 1 有
     */
    private Integer isFixedLocation;
      /**
     * 用户id
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


}
