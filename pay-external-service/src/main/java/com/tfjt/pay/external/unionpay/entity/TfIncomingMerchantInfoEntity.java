package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 银行入网-商户信息
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
@Getter
@Setter
@TableName("tf_incoming_merchant_info")
public class TfIncomingMerchantInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 入网id
     */
    private Long incomingId;

    /**
     * 商户简称
     */
    private String shopShortName;

    /**
     * 法人证件信息
     */
    private Long legalIdCard;

    /**
     * 法人手机号
     */
    private String legalMobile;

    /**
     * 经办人证件信息
     */
    private Long agentIdCard;

    /**
     * 经办人手机
     */
    private String agentMobile;

    /**
     * 数据有效状态（0：有效，1：无效）
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
