package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-10 17:08
 * @email 598482054@qq.com
 */
@Data
public class UnionPayTransferRespDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 收款账户电子账簿id
     */
    @NotBlank(message = "收款电子账簿不能为空")
    private String inBalanceAcctId;

    /**
     * 收款账户电子账簿name
     */
    @NotBlank(message = "收款账户电子账簿名称不能为空")
    private String inBalanceAcctName;

    /**
     * 付款账户电子账簿id
     */
    @NotBlank(message = "付款电子账簿不能为空")
    private String outBalanceAcctId;

    /**
     * 付款账户电子账簿name
     */
    @NotBlank(message = "付款账户电子账簿名称不能为空")
    private String outBalanceAcctName;

    /**
     * amount
     */
    @NotNull(message = "交易金额不能为空")
    private Integer amount;
    /**
     * 交易订单号
     */
    @NotBlank(message = "交易订单号不能为空")
    private String businessOrderNo;

    /**
     * 业务系统id
     */
    @NotBlank(message = "appid不能为空")
    private String appId;

}
