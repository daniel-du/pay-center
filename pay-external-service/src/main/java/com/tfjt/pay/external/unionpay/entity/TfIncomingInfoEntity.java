package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 入网信息
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Getter
@Setter
@TableName("tf_incoming_info")
public class TfIncomingInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 入网id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员id
     */
    private String memberId;

    /**
     * 经销商/供应商id/店铺id
     */
    private Long businessId;

    /**
     * 系统来源
     */
    private Byte businessType;

    /**
     * 入网渠道类型（1：平安，2：银联）
     */
    private Byte accessChannelType;

    /**
     * 入网类型（1：贷款，2：商户入网）
     */
    private Byte accessType;

    /**
     * 平安：子账号、银联：商户ID
     */
    private String accountNo;

    /**
     * 银联企业号
     */
    private String accountNo2;

    /**
     * 入网主体类型（1：个人，2：企业）
     */
    private Byte accessMainType;

    /**
     * 入网时间
     */
    private LocalDateTime accessTime;

    /**
     * 入网状态（1：信息填写，2：入网中，3：入网成功，4：入网失败）
     */
    private Integer accessStatus;

    /**
     * 签约渠道
     */
    private Byte signChannel;

    /**
     * 数据有效状态（0：有效，1：无效）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 失败时间
     */
    private LocalDateTime failTime;

    /**
     * 失败原因
     */
    private String failReason;
}
