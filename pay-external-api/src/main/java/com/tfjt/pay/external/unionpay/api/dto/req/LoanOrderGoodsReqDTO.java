package com.tfjt.pay.external.unionpay.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-15 14:52
 * @email 598482054@qq.com
 */
@Data
public class LoanOrderGoodsReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品金额
     */
    private Integer productAmount;
    /**
     * 商品数量
     */
    private Integer productCount;

}
