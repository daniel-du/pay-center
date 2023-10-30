package com.tfjt.pay.external.unionpay.dto.req;

import lombok.Data;

/**
 * @author tony
 * @version 1.0
 * @title ProductInfoRespDTO
 * @description
 * @create 2023/10/19 13:53
 */
@Data
public class ProductInfoReqDTO {

    /**
     * 订单编号
     * 要求64个字符内，只能是数字、大小写字母和_
     */
    private String orderNo;

    /**
     * 订单金额
     */
    private Long orderAmount;

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
