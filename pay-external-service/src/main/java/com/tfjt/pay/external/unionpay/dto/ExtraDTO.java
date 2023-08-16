package com.tfjt.pay.external.unionpay.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Lzh
 * @version 1.0
 * @title ExtraDTO
 * @description 银联合并消费 扩展参数
 * @Date 2023/8/10 16:59
 */
@Data
public class ExtraDTO implements Serializable {

        /** 订单编号 */
        private String orderNo;

        /** 订单金额 */
        private String orderAmount;

        /** 商品名称 */
        private String productName;

        /** 商品数量 */
        private String productCount;

}
