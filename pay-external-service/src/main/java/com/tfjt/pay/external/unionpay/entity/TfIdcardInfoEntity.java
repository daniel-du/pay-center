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
 * 证件信息
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
@Getter
@Setter
@TableName("tf_idcard_info")
public class TfIdcardInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 证件正面照片
     */
    private String frontIdCardUrl;

    /**
     * 证件反面照片
     */
    private String backIdCardUrl;

    /**
     * 证件手持照片
     */
    private String holdIdCardUrl;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private Boolean sex;

    /**
     * 法人证件类型
     */
    private Integer idType;

    /**
     * 法人证件号码
     */
    private String idNo;

    /**
     * 法人国籍
     */
    private String nationality;

    /**
     * 证件有效起始日期
     */
    private String idEffectiveDate;

    /**
     * 证件有效截止日期
     */
    private String idExpiryDate;

    /**
     * 证件是否长期（0否，1是）
     */
    private Boolean isLongTerm;

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
