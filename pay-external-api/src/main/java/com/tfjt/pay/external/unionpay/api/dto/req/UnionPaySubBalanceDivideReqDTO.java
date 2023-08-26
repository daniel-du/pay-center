package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-14 11:01
 * @email 598482054@qq.com
 */
@Data
public class UnionPaySubBalanceDivideReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**收款电子账簿id*/
    @NotNull(message = "收款电子账簿id不能为空")
    private String recvBalanceAcctId;
    /**收款户名*/
    @NotNull(message = "收款户名不能为空")
    private String recvBalanceAcctName;
    /**交易金额*/
    @NotNull(message = "交易金额不能为空")
    @Min(message = "交易金额不能小于一分",value = 1)
    private Integer amount;
    /**业务系统订单*/
    @NotNull(message = "业务系统订单号不能为空")
    private String subBusinessOrderNo;
    /**交易附言*/
    private String createRemark;

}
