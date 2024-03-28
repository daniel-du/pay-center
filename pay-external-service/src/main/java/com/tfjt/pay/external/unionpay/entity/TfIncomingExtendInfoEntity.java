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
 * 入网扩展信息表
 * </p>
 *
 * @author Du Penglun
 * @since 2024-03-21
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tf_incoming_extend_info")
public class TfIncomingExtendInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 入网主表id
     */
    private Long incomingId;

    /**
     * 实名认证状态
     */
    private Byte authStatus;

    /**
     * 签约状态
     */
    private Byte signStatus;

    /**
     * 绑卡状态
     */
    private Byte bindStatus;

    /**
     * 是否删除，0：否，1：是
     */
    private Byte isDelete;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 变更时间
     */
    private LocalDateTime updateTime;

}
