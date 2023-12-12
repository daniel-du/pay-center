package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 银行入网-营业信息
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-07
 */
@Data
@Builder
@TableName("tf_incoming_business_info")
public class TfIncomingBusinessInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 入网id
     */
    private Long incomingId;

    /**
     * 营业执照信息
     */
    private Long businessLicenseId;

    /**
     * 联系邮箱
     */
    private String email;

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
     * 修改时间
     */
    private LocalDateTime updateTime;
}
