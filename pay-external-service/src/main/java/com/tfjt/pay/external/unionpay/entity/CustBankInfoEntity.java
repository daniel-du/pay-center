package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 客户银行信息
 *
 * @author young
 * @email blank.lee@163.com
 * @date 2023-05-20 09:27:39
 */
@Data
@TableName("tf_cust_bank_info")
public class CustBankInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 银行卡正面
     */
    @NotBlank(message = "银行卡正面照片不能为空")
    private String frontBankCardUrl;
    /**
     * 开户名称
     */
    @NotBlank(message = "开户名称不能为空")
    @Length(min=1,max = 20,message = "开户名称长度要求1~20个字符")
    private String accountName;
    /**
     * 银行卡号
     */
    @NotBlank(message = "银行卡号不能为空")
    @Length(min = 6,max = 34,message ="银行卡号长度不正确")
    private String bankCardNo;
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;
    /**
     * 省
     */
    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "省份名称不能为空")
    private String provinceName;
    /**
     * 市
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "城市名称不能为空")
    private String cityName;
    /**
     * 所属支行银行编码
     */
    @NotBlank(message = "开户银行编码不能为空")
    private String bankCode;

    /**
     * 开户银行联行
     */
    @NotBlank(message = "开户银行联行号不能为空")
    private String bankBranchCode;
    /**
     * 所属支行名称
     */
    @NotBlank(message = "开户名称不能为空")
    private String bankName;
    /**
     * 短信验证码
     */
    @NotBlank(message = "短信验证码不能为空")
    private String smsCode;
    /**
     * 用户id
     */
    @NotNull(message = "贷款用户ID不能为空")
    private Long loanUserId;
    /**
     * 职业
     */
    @NotBlank(message="职业不能为空")
    private String career;
    /**
     * 结算类型 1个人 2企业
     */
    private int settlementType;
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

    @TableField(exist = false)
    private String uuid;

    /**
     * 开户行总行名称
     */
    private String bigBankName;

    private String verifyStatus;

}
