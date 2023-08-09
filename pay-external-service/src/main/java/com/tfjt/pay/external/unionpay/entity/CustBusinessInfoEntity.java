package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
     * 门面照
     */
    @TableField(exist = false)
    private String facadePhotoUrl;
    @TableField(exist = false)
    private Long facadePhotoId;

    /**
     * 店铺室内
     */
    @TableField(exist = false)
    private String shopPhotoUrl;

    @TableField(exist = false)
    private Long shopPhotoId;

    /**
     * 商品照片
     */
    @TableField(exist = false)
    private String goodsPhotoUrl;

    /**
     * 辅助正面材料
     */
    @TableField(exist = false)
    private String auxiliaryPhotoUrl;
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

    /**
     * 商品
     */
    @TableField(exist = false)
    List<CustBusinessAttachInfoEntity> goodsAttachInfoList;

    /**
     * 证明材料
     */
    @TableField(exist = false)
    List<CustBusinessAttachInfoEntity> evidenceAttachInfoList;

}
