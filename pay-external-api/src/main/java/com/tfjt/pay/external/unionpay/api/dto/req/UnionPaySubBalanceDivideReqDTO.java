package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

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
    private String recvBalanceAcctId;
    /**收款户名*/
    private String recvBalanceAcctName;
    /**交易金额*/
    private Integer amount;
    /**业务系统订单*/
    private String subBusinessOrderNo;
    /**交易附言*/
    private String createRemark;

}
