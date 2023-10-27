package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName WithdrawalCreateReqDTO
 * @description: 提现创建
 * @author Lzh
 * @date 2023年08月09日
 * @version: 1.0
 */
@Data
public class WithdrawalCreateReqDTO implements Serializable {
    /**平台订单号*/
    private String outOrderNo;

    /**发送时间 格式:RFC3339*/
    private String sentAt;

    /**金额*/
    private Long amount;

    /**平台手续费*/
    private Integer serviceFee;

    /**电子账簿ID*/
    private String balanceAcctId;

    /**
     * 业务类型
     * {@link com.tfjt.pay.external.unionpay.enums.UnionPayBusinessTypeEnum}
     */
    private String businessType;
    /**
     * 提现目标银行账号
     */
    private String bankAcctNo;
    /**
     * 目标银行账号类型
     */
    private String bankAcctType;

    /**
     * 开户银行联行号
     */
    private String bankBranchCode;

    /**
     * 开户银行编号
     */
    private String bankCode;

    /**
     * 开户名称
     */
    private String name;

    /**
     * 银行附言
     */
    private String bankMemo;

    /**
     * 手机号
     */
    private String mobileNumber;

    /**
     * 交易授权码
     */
    private String password;

    /**
     * 备注
     */
    private String remark;

    /**
     * 扩展字段
     */
    private Map<String,Object> extra;

    /**
     * 自定义参数
     */
    private String metadata;

}
