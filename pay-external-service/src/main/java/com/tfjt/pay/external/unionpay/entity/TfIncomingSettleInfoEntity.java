package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 银行入网-结算信息
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("tf_incoming_settle_info")
public class TfIncomingSettleInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 进件主表id
     */
    private Long incomingId;

    /**
     * 结算账户类型
     */
    private Byte settlementAccountType;

    /**
     * 银行卡信息id
     */
    private Long bankCardId;

    /**
     * 职业
     */
    private String occupation;

    /**
     * 是否默认（1：是，0：否）
     */
    private Byte defaultFlag;

    /**
     * 绑定状态（1：绑定，0：解绑）
     */
    private Byte bindStatus;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 结算id
     */
    private String settlementId;

    /**
     * 标记删除（0：有效，1：无效）
     */
    private Byte isDeleted;

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
}
