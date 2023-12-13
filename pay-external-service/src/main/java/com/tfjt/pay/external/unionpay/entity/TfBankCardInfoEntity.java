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
 * 银行卡信息
 * </p>
 *
 * @author Du Penglun
 * @since 2023-12-11
 */
@Getter
@Setter
@TableName("tf_bank_card_info")
public class TfBankCardInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 开户名称
     */
    private String bankAccountName;

    /**
     * 银行卡正面照片
     */
    private String bankCardUrl;

    /**
     * 银行卡账户
     */
    private String bankCardNo;

    /**
     * 银行预留手机号
     */
    private String bankCardMobile;

    /**
     * 开户所在地-省code
     */
    private String openAccountProvince;

    /**
     * 开户所在地-省
     */
    private String openAccountProvinceName;

    /**
     * 开户所在地-市code
     */
    private String openAccountCity;

    /**
     * 开户所在地-市
     */
    private String openAccountCityName;

    /**
     * 开户所在地-区code
     */
    private String openAccountDistrict;

    /**
     * 开户所在地-区
     */
    private String openAccountDistrictName;

    /**
     * 开户银行联行号
     */
    private String bankBranchCode;

    /**
     * 开户银行编码
     */
    private String bankCode;

    /**
     * 开户总行名称
     */
    private String bankName;

    /**
     * 开户支行名称
     */
    private String bankSubBranchName;

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
