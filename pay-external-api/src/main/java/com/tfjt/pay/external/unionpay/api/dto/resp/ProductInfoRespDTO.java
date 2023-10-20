package com.tfjt.pay.external.unionpay.api.dto.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tony
 * @version 1.0
 * @title ProductInfoRespDTO
 * @description
 * @create 2023/10/19 13:53
 */
@Data
public class ProductInfoRespDTO implements Serializable {

    /**
     * 订单编号
     * 要求64个字符内，只能是数字、大小写字母和_
     */
    private String orderNo;

    /**
     * 订单金额
     */
    private String orderAmount;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品数量
     * 最大9999
     */
    private Integer productCount;


}
