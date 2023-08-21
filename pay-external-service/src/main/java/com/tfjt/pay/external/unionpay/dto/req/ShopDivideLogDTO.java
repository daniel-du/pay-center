package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songx
 * @date 2023-08-20 23:36
 * @email 598482054@qq.com
 */
@Data
public class ShopDivideLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Integer shopId;
    /**
     * 交易金额
     */
    private BigDecimal money;
    /**
     * 记录类型  1店铺 2经销商
     */
    private Integer logType;
    /**
     * 业务类型: 1支付 2提现 3入金  4转账  5退款
     */
    private Integer type;
    /**
     * 状态: 0处理中 1成功  2失败
     */
    private Integer status;
    /**
     * 付款电子账簿id
     */
    private String payBalanceAcctId;
    /**
     * 收款电子账簿id
     */
    private Integer recvBalanceAcctId;
    /**
     * 付款户名
     */
    private String payAccountName;
    /**
     * 收款户名
     */
    private String recvAccountName;
}
