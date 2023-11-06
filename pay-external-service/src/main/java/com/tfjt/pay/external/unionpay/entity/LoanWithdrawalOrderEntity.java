package com.tfjt.pay.external.unionpay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 提现
 * </p>
 *
 * @author young
 * @since 2023-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tf_loan_withdrawal_order")
@ApiModel(value="LoanWithdrawalOrder对象", description="提现")
public class LoanWithdrawalOrderEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "提现订单号")
    private String withdrawalOrderNo;

    @ApiModelProperty(value = "发送时间")
    private Date sendAt;

    @ApiModelProperty(value = "金额")
    private Integer amount;

    @ApiModelProperty(value = "手续费")
    private Integer serviceFee;

    @ApiModelProperty(value = "电子账簿ID")
    private String balanceAcctId;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "提现目标银行账号")
    private String bankAcctNo;

    @ApiModelProperty(value = "目标银行账号类型")
    private String bankAcctType;

    @ApiModelProperty(value = "手机号")
    private String mobileNumber;

    @ApiModelProperty(value = "开户银行联行号")
    private String bankBranchCode;

    @ApiModelProperty(value = "开户名称")
    private String bankName;

    @ApiModelProperty(value = "银行附言")
    private String bankMemo;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "appid")
    private String appId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "银联提现id")
    private String withdrawalId;


}
