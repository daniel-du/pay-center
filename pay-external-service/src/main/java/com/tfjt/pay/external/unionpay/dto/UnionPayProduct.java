package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songx
 * @date 2023-08-16 14:47
 * @email 598482054@qq.com
 */
@Data
public class UnionPayProduct implements Serializable {

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
    private Integer orderAmount;
    /**
     * 商品数量
     */
    private Integer productCount;
}
